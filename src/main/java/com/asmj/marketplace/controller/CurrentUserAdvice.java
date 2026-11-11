package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {
    private final UserAccountRepository users;
    public CurrentUserAdvice(UserAccountRepository users) { this.users = users; }

    @ModelAttribute
    public void addSignedInUser(Principal principal, Model model) {
        if (principal != null) {
            UserAccount user = users.findByEmailIgnoreCase(principal.getName()).orElse(null);
            if (user != null) model.addAttribute("signedInUser", user);
        }
    }
}
