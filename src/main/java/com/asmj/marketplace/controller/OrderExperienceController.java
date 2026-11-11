package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.order.*;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.security.Principal;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderExperienceController {
 private final MarketplaceOrderRepository orders; private final UserAccountRepository users;
 public OrderExperienceController(MarketplaceOrderRepository orders,UserAccountRepository users){this.orders=orders;this.users=users;}
 @GetMapping("/orders/{id}/track") public String track(@PathVariable String id,Principal principal,Model model){MarketplaceOrder order=ownedOrder(id,principal);if(order==null)return "auth/access-denied";if(order.getStatusHistory()==null)order.setStatusHistory(new ArrayList<>());model.addAttribute("order",order);return "order/tracking";}
 @GetMapping("/orders/{id}/invoice") public String invoice(@PathVariable String id,Principal principal,Model model){MarketplaceOrder order=ownedOrder(id,principal);if(order==null)return "auth/access-denied";model.addAttribute("order",order);return "order/invoice";}
 private MarketplaceOrder ownedOrder(String id,Principal principal){UserAccount user=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();MarketplaceOrder order=orders.findById(id).orElse(null);if(order==null)return null;boolean allowed=user.getRole()==Role.ROLE_ADMIN||(user.getRole()==Role.ROLE_CUSTOMER&&user.getId().equals(order.getCustomerUserId()))||(user.getRole()==Role.ROLE_VENDOR&&user.getId().equals(order.getVendorUserId()));return allowed?order:null;}
}
