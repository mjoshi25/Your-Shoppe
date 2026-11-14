package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.advertising.AdvertisingService;
import com.asmj.marketplace.advertising.AdCampaign;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {
    private final UserAccountRepository users; private final AdvertisingService advertising;
    public CurrentUserAdvice(UserAccountRepository users, AdvertisingService advertising) { this.users = users; this.advertising = advertising; }

    @ModelAttribute
    public void addSignedInUser(Principal principal, Model model, HttpServletRequest request) {
        Map<String,List<AdCampaign>> slots = new LinkedHashMap<>();
        // Thymeleaf templates address these map entries by placement code.
        // Seed every known key so an empty campaign inventory renders safely.
        for (String placement : List.of("HOME_HERO", "HOME_CARD", "CATEGORY_BANNER",
                "SEARCH_SPONSORED", "PRODUCT_DETAIL", "CART_BANNER")) {
            slots.put(placement, new ArrayList<>());
        }
        String categoryId=request.getParameter("categoryId");
        jakarta.servlet.http.HttpSession session=request.getSession(false);
        String targetPin=session==null?null:(String)session.getAttribute("asmjAdTargetPin");
        for (AdCampaign campaign : advertising.allLive(targetPin,categoryId)) {
            slots.computeIfAbsent(campaign.getPlacementCode(), k -> new ArrayList<>()).add(campaign);
        }
        model.addAttribute("adTargetPin",targetPin==null?"":targetPin);
        model.addAttribute("adsByPlacement", slots);
        if (principal != null) {
            UserAccount user = users.findByEmailIgnoreCase(principal.getName()).orElse(null);
            if (user != null) model.addAttribute("signedInUser", user);
        }
    }
}
