package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.VendorManagementService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final UserAccountRepository users;
    private final VendorManagementService vendorManagement;

    public DashboardController(UserAccountRepository users, VendorManagementService vendorManagement) {
        this.users = users;
        this.vendorManagement = vendorManagement;
    }

    @GetMapping("/")
    public String home() { return "home"; }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal) {
        UserAccount user = users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        if (user.getRole() == Role.ROLE_ADMIN) return "redirect:/admin/dashboard";
        if (user.getRole() == Role.ROLE_VENDOR) {
            return user.getVendorStatus() == VendorStatus.APPROVED
                    ? "redirect:/vendor/dashboard" : "redirect:/vendor/pending";
        }
        if (user.getRole() == Role.ROLE_CUSTOMER) return "redirect:/customer/dashboard";
        return "redirect:/login?error";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Admin dashboard");
        model.addAttribute("totalVendors", vendorManagement.findVendors("ALL", "").size());
        model.addAttribute("pendingVendors", vendorManagement.count(VendorStatus.PENDING));
        model.addAttribute("approvedVendors", vendorManagement.count(VendorStatus.APPROVED));
        model.addAttribute("rejectedVendors", vendorManagement.count(VendorStatus.REJECTED));
        model.addAttribute("suspendedVendors", vendorManagement.count(VendorStatus.SUSPENDED));
        return "dashboard";
    }

    @GetMapping("/vendor/dashboard")
    public String vendorDashboard(Principal principal, Model model) {
        UserAccount user = users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        if (user.getVendorStatus() != VendorStatus.APPROVED) return "redirect:/vendor/pending";
        model.addAttribute("pageTitle", "Vendor dashboard");
        return "dashboard";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model) {
        model.addAttribute("pageTitle", "Customer dashboard");
        return "dashboard";
    }

    @GetMapping("/vendor/pending")
    public String vendorPending(Principal principal, Model model) {
        UserAccount user = users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        model.addAttribute("vendorStatus", user.getVendorStatus());
        return "vendor/pending";
    }
}
