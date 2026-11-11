package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.audit.AuditService;
import com.asmj.marketplace.chat.VendorApplicationConversationRepository;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.VendorProfileRepository;
import com.asmj.marketplace.service.VendorManagementService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/vendors")
public class AdminVendorController {
    private final VendorManagementService vendors;
    private final VendorProfileRepository profiles;
    private final VendorApplicationConversationRepository applicationChats;
    private final AuditService audit;

    public AdminVendorController(VendorManagementService vendors, VendorProfileRepository profiles, VendorApplicationConversationRepository applicationChats, AuditService audit) {
        this.vendors = vendors;
        this.profiles = profiles;
        this.applicationChats = applicationChats;
        this.audit = audit;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "ALL") String status,
                       @RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("vendors", vendors.findVendors(status, q));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("query", q);
        model.addAttribute("pendingCount", vendors.count(VendorStatus.PENDING));
        model.addAttribute("approvedCount", vendors.count(VendorStatus.APPROVED));
        model.addAttribute("rejectedCount", vendors.count(VendorStatus.REJECTED));
        model.addAttribute("suspendedCount", vendors.count(VendorStatus.SUSPENDED));
        model.addAttribute("totalCount", vendors.findVendors("ALL", "").size());
        return "admin/vendors";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        var vendor = vendors.getVendor(id);
        VendorProfile profile = profiles.findByUserId(id).orElse(null);
        model.addAttribute("vendor", vendor);
        model.addAttribute("profile", profile);
        model.addAttribute("history", vendors.history(id));
        var applicationChat = applicationChats.findByVendorUserId(id).orElse(null);
        model.addAttribute("applicationChat", applicationChat);
        return "admin/vendor-detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable String id, @RequestParam(required = false) String reason,
                          Principal principal, RedirectAttributes redirect) {
        return perform(id, () -> vendors.approve(id, principal.getName(), reason),
                "Vendor approved successfully.", redirect, principal.getName(), "APPROVED");
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable String id, @RequestParam String reason,
                         Principal principal, RedirectAttributes redirect) {
        return perform(id, () -> vendors.reject(id, principal.getName(), reason),
                "Vendor application rejected.", redirect, principal.getName(), "REJECTED");
    }

    @PostMapping("/{id}/suspend")
    public String suspend(@PathVariable String id, @RequestParam String reason,
                          Principal principal, RedirectAttributes redirect) {
        return perform(id, () -> vendors.suspend(id, principal.getName(), reason),
                "Vendor suspended. New selling access is blocked.", redirect, principal.getName(), "SUSPENDED");
    }

    @PostMapping("/{id}/reactivate")
    public String reactivate(@PathVariable String id, @RequestParam(required = false) String reason,
                             Principal principal, RedirectAttributes redirect) {
        return perform(id, () -> vendors.reactivate(id, principal.getName(), reason),
                "Vendor reactivated successfully.", redirect, principal.getName(), "REACTIVATED");
    }

    private String perform(String id, Runnable action, String success, RedirectAttributes redirect, String actor, String actionName) {
        try {
            action.run();
            audit.record(actor,"ADMIN","VENDOR_"+actionName,"VENDOR",id,success);
            redirect.addFlashAttribute("successMessage", success);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/vendors/" + id;
    }
}
