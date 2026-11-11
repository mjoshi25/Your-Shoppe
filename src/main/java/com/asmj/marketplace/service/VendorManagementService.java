package com.asmj.marketplace.service;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorReviewEvent;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorReviewEventRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VendorManagementService {
    private final UserAccountRepository users;
    private final VendorReviewEventRepository events;

    public VendorManagementService(UserAccountRepository users, VendorReviewEventRepository events) {
        this.users = users;
        this.events = events;
    }

    public List<UserAccount> findVendors(String status, String query) {
        List<UserAccount> vendors = users.findByRoleOrderByCreatedAtDesc(Role.ROLE_VENDOR);
        VendorStatus selected = null;
        if (StringUtils.hasText(status) && !"ALL".equalsIgnoreCase(status)) {
            try { selected = VendorStatus.valueOf(status.toUpperCase()); }
            catch (IllegalArgumentException ignored) { selected = null; }
        }
        final VendorStatus filterStatus = selected;
        String term = query == null ? "" : query.trim().toLowerCase();
        return vendors.stream()
                .filter(v -> filterStatus == null || v.getVendorStatus() == filterStatus)
                .filter(v -> term.isEmpty()
                        || safe(v.getFullName()).contains(term)
                        || safe(v.getEmail()).contains(term)
                        || safe(v.getMobile()).contains(term))
                .toList();
    }

    public UserAccount getVendor(String id) {
        UserAccount vendor = users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor account not found."));
        if (vendor.getRole() != Role.ROLE_VENDOR) {
            throw new IllegalArgumentException("The selected account is not a vendor.");
        }
        return vendor;
    }

    public long count(VendorStatus status) {
        return users.countByRoleAndVendorStatus(Role.ROLE_VENDOR, status);
    }

    public List<VendorReviewEvent> history(String vendorId) {
        return events.findByVendorUserIdOrderByCreatedAtDesc(vendorId);
    }

    public void approve(String vendorId, String adminEmail, String reason) {
        transition(vendorId, adminEmail, VendorStatus.APPROVED, reason, false);
    }

    public void reject(String vendorId, String adminEmail, String reason) {
        transition(vendorId, adminEmail, VendorStatus.REJECTED, reason, true);
    }

    public void suspend(String vendorId, String adminEmail, String reason) {
        transition(vendorId, adminEmail, VendorStatus.SUSPENDED, reason, true);
    }

    public void reactivate(String vendorId, String adminEmail, String reason) {
        UserAccount vendor = getVendor(vendorId);
        if (vendor.getVendorStatus() != VendorStatus.SUSPENDED) {
            throw new IllegalArgumentException("Only suspended vendors can be reactivated.");
        }
        transition(vendorId, adminEmail, VendorStatus.APPROVED, reason, false);
    }

    private void transition(String vendorId, String adminEmail, VendorStatus target,
                            String reason, boolean reasonRequired) {
        if (reasonRequired && !StringUtils.hasText(reason)) {
            throw new IllegalArgumentException("Please provide a reason for this action.");
        }
        UserAccount vendor = getVendor(vendorId);
        VendorStatus previous = vendor.getVendorStatus();

        if (previous == target) {
            throw new IllegalArgumentException("Vendor is already " + target.name().toLowerCase() + ".");
        }
        if (target == VendorStatus.SUSPENDED && previous != VendorStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved vendors can be suspended.");
        }
        if (target == VendorStatus.REJECTED && previous != VendorStatus.PENDING) {
            throw new IllegalArgumentException("Only pending vendor applications can be rejected.");
        }
        if (target == VendorStatus.APPROVED && previous != VendorStatus.PENDING
                && previous != VendorStatus.REJECTED && previous != VendorStatus.SUSPENDED) {
            throw new IllegalArgumentException("This vendor cannot be approved from the current status.");
        }

        vendor.setVendorStatus(target);
        vendor.setEnabled(true);
        users.save(vendor);

        VendorReviewEvent event = new VendorReviewEvent();
        event.setVendorUserId(vendor.getId());
        event.setFromStatus(previous);
        event.setToStatus(target);
        event.setReviewedBy(adminEmail);
        event.setReason(reason == null ? "" : reason.trim());
        event.setCreatedAt(Instant.now());
        events.save(event);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
