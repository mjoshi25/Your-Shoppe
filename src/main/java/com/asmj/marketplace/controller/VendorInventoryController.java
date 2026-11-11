package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.product.*;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.security.Principal;
import java.time.Instant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VendorInventoryController {
 private final UserAccountRepository users; private final ProductRepository products; private final StockMovementRepository movements;
 public VendorInventoryController(UserAccountRepository users,ProductRepository products,StockMovementRepository movements){this.users=users;this.products=products;this.movements=movements;}
 @GetMapping("/vendor/inventory") public String inventory(Principal principal,Model model){UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();var list=products.findByVendorUserIdOrderByCreatedAtDesc(vendor.getId());model.addAttribute("products",list);model.addAttribute("lowStockCount",list.stream().filter(p->p.getStatus()==ProductStatus.ACTIVE&&p.getStockQuantity()!=null&&p.getStockQuantity()<=5).count());model.addAttribute("movements",movements.findTop100ByVendorUserIdOrderByCreatedAtDesc(vendor.getId()));return "vendor/inventory";}
 @PostMapping("/vendor/inventory/{id}/adjust") public String adjust(@PathVariable String id,@RequestParam Integer newQuantity,@RequestParam(required=false) String reason,Principal principal,RedirectAttributes redirect){UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();Product product=products.findById(id).orElseThrow();if(!vendor.getId().equals(product.getVendorUserId()))return "auth/access-denied";if(newQuantity==null||newQuantity<0||newQuantity>1000000){redirect.addFlashAttribute("errorMessage","Enter a stock quantity between 0 and 1,000,000.");return "redirect:/vendor/inventory";}int old=product.getStockQuantity()==null?0:product.getStockQuantity();StockMovement movement=new StockMovement();movement.setProductId(product.getId());movement.setVendorUserId(vendor.getId());movement.setProductName(product.getName());movement.setPreviousQuantity(old);movement.setNewQuantity(newQuantity);movement.setReason(reason==null?"Manual stock adjustment":reason.trim());movement.setChangedBy(vendor.getFullName());movement.setCreatedAt(Instant.now());movements.save(movement);product.setStockQuantity(newQuantity);product.setStockUpdatedAt(Instant.now());products.save(product);redirect.addFlashAttribute("successMessage","Stock updated and movement recorded.");return "redirect:/vendor/inventory";}
}
