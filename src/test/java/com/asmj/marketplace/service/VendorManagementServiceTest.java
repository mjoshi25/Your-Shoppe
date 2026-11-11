package com.asmj.marketplace.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorReviewEvent;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorReviewEventRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VendorManagementServiceTest {
    private UserAccountRepository users;
    private VendorReviewEventRepository events;
    private VendorManagementService service;

    @BeforeEach
    void setUp() {
        users = mock(UserAccountRepository.class);
        events = mock(VendorReviewEventRepository.class);
        service = new VendorManagementService(users, events);
    }

    private UserAccount vendor(VendorStatus status) {
        UserAccount account = new UserAccount();
        account.setId("vendor-1");
        account.setRole(Role.ROLE_VENDOR);
        account.setVendorStatus(status);
        account.setEmail("vendor@example.com");
        return account;
    }

    @Test
    void approvesPendingVendorAndRecordsAuditEvent() {
        UserAccount vendor = vendor(VendorStatus.PENDING);
        when(users.findById("vendor-1")).thenReturn(Optional.of(vendor));

        service.approve("vendor-1", "admin@example.com", "Documents checked");

        assertEquals(VendorStatus.APPROVED, vendor.getVendorStatus());
        verify(users).save(vendor);
        verify(events).save(argThat(event ->
                event.getFromStatus() == VendorStatus.PENDING
                && event.getToStatus() == VendorStatus.APPROVED
                && event.getReviewedBy().equals("admin@example.com")));
    }

    @Test
    void rejectsPendingVendorWhenReasonProvided() {
        UserAccount vendor = vendor(VendorStatus.PENDING);
        when(users.findById("vendor-1")).thenReturn(Optional.of(vendor));

        service.reject("vendor-1", "admin@example.com", "Incomplete business details");

        assertEquals(VendorStatus.REJECTED, vendor.getVendorStatus());
        verify(events).save(any(VendorReviewEvent.class));
    }

    @Test
    void requiresReasonToRejectOrSuspend() {
        UserAccount vendor = vendor(VendorStatus.PENDING);
        when(users.findById("vendor-1")).thenReturn(Optional.of(vendor));

        assertThrows(IllegalArgumentException.class,
                () -> service.reject("vendor-1", "admin@example.com", " "));
        verify(users, never()).save(any(UserAccount.class));
    }

    @Test
    void suspendedVendorCanBeReactivated() {
        UserAccount vendor = vendor(VendorStatus.SUSPENDED);
        when(users.findById("vendor-1")).thenReturn(Optional.of(vendor));

        service.reactivate("vendor-1", "admin@example.com", "Issue resolved");

        assertEquals(VendorStatus.APPROVED, vendor.getVendorStatus());
    }
}
