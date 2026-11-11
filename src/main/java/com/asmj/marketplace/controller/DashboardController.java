package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.chat.VendorApplicationConversationRepository;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.VendorManagementService;
import com.asmj.marketplace.order.*;
import com.asmj.marketplace.product.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
 private final UserAccountRepository users; private final VendorManagementService vendorManagement; private final VendorApplicationConversationRepository applicationChats; private final MarketplaceOrderRepository orders; private final ProductRepository products; private final ProductReviewRepository reviews;
 public DashboardController(UserAccountRepository users,VendorManagementService vendorManagement,VendorApplicationConversationRepository applicationChats,MarketplaceOrderRepository orders,ProductRepository products,ProductReviewRepository reviews){this.users=users;this.vendorManagement=vendorManagement;this.applicationChats=applicationChats;this.orders=orders;this.products=products;this.reviews=reviews;}
 @GetMapping("/") public String home(){return "home";}
 @GetMapping("/dashboard") public String dashboard(Principal principal){UserAccount user=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();if(user.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/dashboard";if(user.getRole()==Role.ROLE_VENDOR)return user.getVendorStatus()==VendorStatus.APPROVED?"redirect:/vendor/dashboard":"redirect:/vendor/pending";if(user.getRole()==Role.ROLE_CUSTOMER)return "redirect:/customer/dashboard";return "redirect:/login?error";}
 @GetMapping("/admin/dashboard") public String adminDashboard(Model model){model.addAttribute("pageTitle","Admin dashboard");model.addAttribute("totalVendors",vendorManagement.findVendors("ALL","").size());model.addAttribute("pendingVendors",vendorManagement.count(VendorStatus.PENDING));model.addAttribute("approvedVendors",vendorManagement.count(VendorStatus.APPROVED));model.addAttribute("rejectedVendors",vendorManagement.count(VendorStatus.REJECTED));model.addAttribute("suspendedVendors",vendorManagement.count(VendorStatus.SUSPENDED));model.addAttribute("totalOrders",orders.count());model.addAttribute("totalProducts",products.count());model.addAttribute("pendingReviews",reviews.findByStatusOrderByCreatedAtDesc(ProductReview.ReviewStatus.PENDING).size());return "dashboard";}
 @GetMapping("/vendor/dashboard") public String vendorDashboard(Principal principal,Model model){UserAccount user=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();if(user.getVendorStatus()!=VendorStatus.APPROVED)return "redirect:/vendor/pending";List<MarketplaceOrder> vendorOrders=orders.findByVendorUserIdOrderByCreatedAtDesc(user.getId());List<Product> vendorProducts=products.findByVendorUserIdOrderByCreatedAtDesc(user.getId());BigDecimal completedSales=vendorOrders.stream().filter(o->o.getStatus()==OrderStatus.COMPLETED&&o.getPaymentStatus()==PaymentStatus.VERIFIED).map(MarketplaceOrder::getTotal).filter(v->v!=null).reduce(BigDecimal.ZERO,BigDecimal::add);long lowStock=vendorProducts.stream().filter(p->p.getStatus()==ProductStatus.ACTIVE&&p.getStockQuantity()!=null&&p.getStockQuantity()<=5).count();model.addAttribute("pageTitle","Vendor dashboard");model.addAttribute("vendorOrderCount",vendorOrders.size());model.addAttribute("vendorProductCount",vendorProducts.size());model.addAttribute("lowStockCount",lowStock);model.addAttribute("completedSales",completedSales);model.addAttribute("recentVendorOrders",vendorOrders.stream().limit(5).toList());return "dashboard";}
 @GetMapping("/customer/dashboard") public String customerDashboard(Principal principal,Model model){UserAccount user=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();model.addAttribute("pageTitle","Customer dashboard");model.addAttribute("customerOrderCount",orders.findByCustomerUserIdOrderByCreatedAtDesc(user.getId()).size());return "dashboard";}
 @GetMapping({"/vendor/pending","/vendor/application-chat"}) public String vendorPending(Principal principal,Model model){UserAccount user=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();model.addAttribute("vendorStatus",user.getVendorStatus());model.addAttribute("applicationChat",applicationChats.findByVendorUserId(user.getId()).orElse(null));return "vendor/pending";}
}
