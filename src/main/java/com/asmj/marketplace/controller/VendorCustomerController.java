package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.order.*;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendorCustomerController {
 private final UserAccountRepository users; private final MarketplaceOrderRepository orders;
 public VendorCustomerController(UserAccountRepository users, MarketplaceOrderRepository orders){this.users=users;this.orders=orders;}
 @GetMapping("/vendor/customers")
 public String customers(Principal principal, Model model){
  UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
  List<MarketplaceOrder> vendorOrders=orders.findByVendorUserIdOrderByCreatedAtDesc(vendor.getId());
  Map<String,List<MarketplaceOrder>> grouped=new LinkedHashMap<>();
  for(MarketplaceOrder order:vendorOrders){if(order.getCustomerUserId()!=null)grouped.computeIfAbsent(order.getCustomerUserId(),k->new ArrayList<>()).add(order);}
  List<Map<String,Object>> rows=new ArrayList<>();
  for(var entry:grouped.entrySet()){
   List<MarketplaceOrder> customerOrders=entry.getValue(); MarketplaceOrder latest=customerOrders.get(0);
   BigDecimal completed=customerOrders.stream().filter(o->o.getStatus()==OrderStatus.COMPLETED&&o.getPaymentStatus()==PaymentStatus.VERIFIED).map(MarketplaceOrder::getTotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
   Map<String,Object> row=new LinkedHashMap<>();row.put("name",latest.getCustomerName());row.put("email",latest.getCustomerEmail());row.put("mobile",latest.getCustomerMobile());row.put("orderCount",customerOrders.size());row.put("lastOrder",latest.getCreatedAt());row.put("completedSpend",completed);row.put("latestStatus",latest.getStatus());rows.add(row);
  }
  model.addAttribute("customers",rows);model.addAttribute("customerCount",rows.size());model.addAttribute("orderCount",vendorOrders.size());return "vendor/customers";
 }
}
