package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.complaints.OrderComplaintRepository;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.notification.*;
import com.asmj.marketplace.order.MarketplaceOrderRepository;
import com.asmj.marketplace.approval.ApprovalCaseRepository;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NotificationController {
 private final NotificationService service; private final UserAccountRepository users; private final MarketplaceOrderRepository orders; private final ApprovalCaseRepository approvals; private final OrderComplaintRepository complaints; private final com.asmj.marketplace.advertising.AdCampaignRepository adCampaigns;
 public NotificationController(NotificationService service,UserAccountRepository users,MarketplaceOrderRepository orders,ApprovalCaseRepository approvals,OrderComplaintRepository complaints,com.asmj.marketplace.advertising.AdCampaignRepository adCampaigns){this.service=service;this.users=users;this.orders=orders;this.approvals=approvals;this.complaints=complaints;this.adCampaigns=adCampaigns;}
 private UserAccount current(Principal p){return users.findByEmailIgnoreCase(p.getName()).orElseThrow();}
 @GetMapping("/notifications") public String list(Principal p,Model m){UserAccount u=current(p);m.addAttribute("notifications",service.recent(u.getId()));m.addAttribute("unreadCount",service.unread(u.getId()));return "notifications/index";}
 @PostMapping("/notifications/read-all") public String readAll(Principal p){service.markAllRead(current(p).getId());return "redirect:/notifications";}
 @GetMapping("/notifications/{id}/open") public String open(@PathVariable String id,Principal p,RedirectAttributes ra){UserAccount u=current(p);MarketplaceNotification n=service.owned(id,u.getId());service.markRead(n);String type=n.getTargetType()==null?"":n.getTargetType().toUpperCase();String target=n.getTargetId();
  if("APPROVAL".equals(type)&&target!=null){var c=approvals.findById(target).orElse(null);if(c==null){ra.addFlashAttribute("errorMessage","This approval request is no longer available.");return "redirect:/notifications";}if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/approval-center/"+target;if(u.getId().equals(c.getOwnerUserId()))return "redirect:/my-approval-requests/"+target;return "auth/access-denied";}
  if("ORDER".equals(type)&&target!=null){var o=orders.findById(target).orElse(null);if(o==null){ra.addFlashAttribute("errorMessage","This order is no longer available.");return "redirect:/notifications";}if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/orders";if(u.getRole()==Role.ROLE_VENDOR&&u.getId().equals(o.getVendorUserId()))return "redirect:/vendor/orders";if(u.getRole()==Role.ROLE_CUSTOMER&&u.getId().equals(o.getCustomerUserId()))return "redirect:/customer/orders";return "auth/access-denied";}
  if("COMPLAINT".equals(type)&&target!=null){var c=complaints.findById(target).orElse(null);if(c==null){ra.addFlashAttribute("errorMessage","This complaint is no longer available.");return "redirect:/notifications";}if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/complaints";if(u.getRole()==Role.ROLE_VENDOR&&u.getId().equals(c.getVendorUserId()))return "redirect:/vendor/complaints";if(u.getRole()==Role.ROLE_CUSTOMER&&u.getId().equals(c.getCustomerUserId()))return "redirect:/customer/complaints";return "auth/access-denied";}
  if("VENDOR_APPLICATION_CHAT".equals(type)&&target!=null){if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/vendors/"+target;if(u.getRole()==Role.ROLE_VENDOR&&u.getId().equals(target))return "redirect:/vendor/pending";return "auth/access-denied";}
  if("AD_CAMPAIGN".equals(type)&&target!=null){var c=adCampaigns.findById(target).orElse(null);if(c==null){ra.addFlashAttribute("errorMessage","This advertisement is no longer available.");return "redirect:/notifications";}if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/advertising";if(u.getRole()==Role.ROLE_VENDOR&&u.getId().equals(c.getVendorUserId()))return "redirect:/vendor/advertising";return "auth/access-denied";}
  ra.addFlashAttribute("infoMessage","This notification does not have an available action page.");return "redirect:/notifications";
 }
}
