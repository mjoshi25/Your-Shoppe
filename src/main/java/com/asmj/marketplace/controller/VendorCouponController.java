package com.asmj.marketplace.controller;

import com.asmj.marketplace.engagement.CouponRepository;
import com.asmj.marketplace.engagement.DiscountCoupon;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VendorCouponController {
    private final CouponRepository coupons; private final UserAccountRepository users;
    private final VendorProfileRepository profiles; private final ProductRepository products;
    public VendorCouponController(CouponRepository c, UserAccountRepository u, VendorProfileRepository p, ProductRepository pr) { coupons=c; users=u; profiles=p; products=pr; }
    @GetMapping("/vendor/coupons") public String page(Principal principal, Model model) {
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        VendorProfile profile=profiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new);
        List<DiscountCoupon> available=coupons.findAllByOrderByCreatedAtDesc().stream().filter(this::currentlyAvailable).toList();
        model.addAttribute("coupons",available); model.addAttribute("acceptedCouponCodes",profile.getAcceptedCouponCodes());
        model.addAttribute("products",products.findByVendorUserIdOrderByCreatedAtDesc(vendor.getId()));
        return "vendor/coupons";
    }
    @PostMapping("/vendor/coupons/participation") public String participation(@RequestParam(required=false) List<String> couponCodes, Principal principal, RedirectAttributes ra) {
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        VendorProfile profile=profiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new); profile.setUserId(vendor.getId());
        List<String> valid=new ArrayList<>(); if(couponCodes!=null) for(String code:couponCodes){String normalized=code.trim().toUpperCase(Locale.ROOT);if(coupons.findByCodeIgnoreCase(normalized).filter(this::currentlyAvailable).isPresent()&&!valid.contains(normalized))valid.add(normalized);}
        profile.setAcceptedCouponCodes(valid); profile.setUpdatedAt(Instant.now()); profiles.save(profile);
        ra.addFlashAttribute("successMessage","Your coupon participation preferences have been saved. Changes apply to new orders only."); return "redirect:/vendor/coupons";
    }
    @PostMapping("/vendor/coupons/products/{id}/exclude") public String exclusion(@PathVariable String id,@RequestParam(defaultValue="false") boolean excluded,Principal principal,RedirectAttributes ra){
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow(); Product product=products.findById(id).orElseThrow();
        if(!vendor.getId().equals(product.getVendorUserId())){ra.addFlashAttribute("errorMessage","You cannot update another vendor's product.");return "redirect:/vendor/coupons";}
        product.setCouponExcluded(excluded);products.save(product);ra.addFlashAttribute("successMessage","Product coupon eligibility updated for future orders.");return "redirect:/vendor/coupons";
    }
    private boolean currentlyAvailable(DiscountCoupon c){Instant now=Instant.now();return c.isActive()&&(c.getStartsAt()==null||!c.getStartsAt().isAfter(now))&&(c.getEndsAt()==null||!c.getEndsAt().isBefore(now))&&(c.getUsageLimit()==null||c.getUsageCount()<c.getUsageLimit());}
}
