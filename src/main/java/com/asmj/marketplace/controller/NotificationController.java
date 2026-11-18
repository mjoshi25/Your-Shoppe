package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Role;
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
 private final NotificationService service; private final UserAccountRepository users; private final MarketplaceOrderRepository orders; private final ApprovalCaseRepository approvals;
 public NotificationController(NotificationService service,UserAccountRepository users,MarketplaceOrderRepository orders,ApprovalCaseRepository approvals){this.service=service;this.users=users;this.orders=orders;this.approvals=approvals;}
 private UserAccount current(Principal p){return users.findByEmailIgnoreCase(p.getName()).orElseThrow();}
 @GetMapping("/notifications") public String list(Principal p,Model m){UserAccount u=current(p);m.addAttribute("notifications",service.recent(u.getId()));m.addAttribute("unreadCount",service.unread(u.getId()));return "notifications/index";}
 @PostMapping("/notifications/read-all") public String readAll(Principal p){service.markAllRead(current(p).getId());return "redirect:/notifications";}
 @GetMapping("/notifications/{id}/open") public String open(@PathVariable String id,Principal p,RedirectAttributes ra){UserAccount u=current(p);MarketplaceNotification n=service.owned(id,u.getId());service.markRead(n);String type=n.getTargetType()==null?"":n.getTargetType().toUpperCase();String target=n.getTargetId();
  if("APPROVAL".equals(type)&&target!=null){var c=approvals.findById(target).orElse(null);if(c==null){ra.addFlashAttribute("errorMessage","This approval request is no longer available.");return "redirect:/notifications";}if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/approval-center/"+target;if(u.getId().equals(c.getOwnerUserId()))return "redirect:/my-approval-requests/"+target;return "auth/access-denied";}
  if("ORDER".equals(type)&&target!=null){var o=orders.findById(target).orElse(null);if(o==null){ra.addFlashAttribute("errorMessage","This order is no longer available.");return "redirect:/notifications";}if(u.getRole()==Role.ROLE_ADMIN)return "redirect:/admin/orders";if(u.getRole()==Role.ROLE_VENDOR&&u.getId().equals(o.getVendorUserId()))return "redirect:/vendor/orders";if(u.getRole()==Role.ROLE_CUSTOMER&&u.getId().equals(o.getCustomerUserId()))return "redirect:/customer/orders";return "auth/access-denied";}
  ra.addFlashAttribute("infoMessage","This notification does not have an available action page.");return "redirect:/notifications";
 }
}
