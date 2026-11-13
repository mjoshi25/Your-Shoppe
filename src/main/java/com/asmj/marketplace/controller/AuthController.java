package com.asmj.marketplace.controller;

import com.asmj.marketplace.dto.RegistrationForm;
import com.asmj.marketplace.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/terms")
    public String terms() { return "auth/terms"; }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)
                ? "redirect:/dashboard" : "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("form", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register/customer")
    public String registerCustomer(@Valid @ModelAttribute("form") RegistrationForm form,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/register";
        try {
            registrationService.registerCustomer(form);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "auth/register";
        } catch (DuplicateKeyException ex) {
            model.addAttribute("registrationError", "An account with this email already exists. Please sign in or use another email.");
            return "auth/register";
        } catch (DataAccessException | IllegalStateException ex) {
            log.error("Customer registration could not be saved to MongoDB", ex);
            model.addAttribute("registrationError", "We could not save your account to the database. Please try again. If the problem continues, contact the administrator.");
            return "auth/register";
        }
    }

    @PostMapping("/register/vendor")
    public String registerVendor(@Valid @ModelAttribute("form") RegistrationForm form,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) return "auth/register";
        try {
            registrationService.registerVendor(form);
            return "redirect:/login?vendorPending";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "auth/register";
        } catch (DuplicateKeyException ex) {
            model.addAttribute("registrationError", "An account with this email already exists. Please sign in or use another email.");
            return "auth/register";
        } catch (DataAccessException | IllegalStateException ex) {
            log.error("Vendor registration could not be saved completely to MongoDB", ex);
            model.addAttribute("registrationError", "We could not save your vendor application completely. No incomplete account should be used to sign in. Please retry or contact the administrator.");
            return "auth/register";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() { return "auth/access-denied"; }
}
