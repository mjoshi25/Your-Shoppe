package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {
    private final UserAccountRepository users;
    private final VendorProfileRepository vendorProfiles;
    private final CloudinaryImageService uploads;
    private final PasswordEncoder passwordEncoder;
    @org.springframework.beans.factory.annotation.Value("${google.maps.browser-api-key:}") private String googleMapsBrowserKey;

    public SettingsController(UserAccountRepository users, VendorProfileRepository vendorProfiles,
                              CloudinaryImageService uploads, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.vendorProfiles = vendorProfiles;
        this.uploads = uploads;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        UserAccount user = current(principal);
        model.addAttribute("googleMapsBrowserKey", googleMapsBrowserKey);
        model.addAttribute("currentUser", user);
        model.addAttribute("roleLabel", roleLabel(user.getRole()));
        if (user.getRole() == Role.ROLE_VENDOR) {
            model.addAttribute("vendorProfile", vendorProfiles.findByUserId(user.getId()).orElseGet(() -> {
                VendorProfile profile = new VendorProfile(); profile.setUserId(user.getId()); return profile;
            }));
        }
        return "settings/index";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(Principal principal,
            @RequestParam String fullName, @RequestParam(required=false) String mobile,
            @RequestParam(required=false) String addressLine, @RequestParam(required=false) String city,
            @RequestParam(required=false) String state, @RequestParam(required=false) String pinCode,
            @RequestParam(required=false) List<String> emailNotifications,
            @RequestParam(required=false) MultipartFile profilePhoto, @RequestParam(defaultValue="false") boolean removeProfilePhoto,
            @RequestParam(required=false) String businessName,
            @RequestParam(required=false) String businessDescription,
            @RequestParam(required=false) String businessAddress,
            @RequestParam(required=false) String businessCity,
            @RequestParam(required=false) String businessState,
            @RequestParam(required=false) String businessPinCode,
            RedirectAttributes ra) {
        UserAccount user = current(principal);
        String name = fullName == null ? "" : fullName.trim();
        if (name.length() < 2 || name.length() > 100) {
            ra.addFlashAttribute("errorMessage", "Please enter a name between 2 and 100 characters.");
            return "redirect:/settings";
        }
        if (mobile != null && !mobile.isBlank() && !mobile.trim().matches("[+0-9() -]{8,20}")) {
            ra.addFlashAttribute("errorMessage", "Please enter a valid mobile number.");
            return "redirect:/settings";
        }
        try {
            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                Map<String,Object> uploaded = uploads.uploadProfilePhoto(profilePhoto);
                user.setProfileImageUrl(String.valueOf(uploaded.get("secure_url")));
            } else if (removeProfilePhoto) {
                user.setProfileImageUrl(null);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/settings";
        }
        user.setFullName(name);
        if (mobile != null) user.setMobile(clean(mobile));
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            user.setAddressLine(clean(addressLine)); user.setCity(clean(city));
            user.setState(clean(state)); user.setPinCode(clean(pinCode));
        }
        user.setEmailNotifications(emailNotifications != null && emailNotifications.contains("true"));
        users.save(user);

        if (user.getRole() == Role.ROLE_VENDOR) {
            VendorProfile profile = vendorProfiles.findByUserId(user.getId()).orElseGet(VendorProfile::new);
            profile.setUserId(user.getId());
            if (businessName != null) profile.setBusinessName(clean(businessName));
            if (businessDescription != null) profile.setBusinessDescription(clean(businessDescription));
            if (businessAddress != null) profile.setAddressLine(clean(businessAddress));
            if (businessCity != null) profile.setCity(clean(businessCity));
            if (businessState != null) profile.setState(clean(businessState));
            if (businessPinCode != null) profile.setPinCode(clean(businessPinCode));
            profile.setUpdatedAt(Instant.now());
            vendorProfiles.save(profile);
        }
        ra.addFlashAttribute("successMessage", "Your profile settings have been updated.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(Principal principal, @RequestParam String currentPassword,
                                 @RequestParam String newPassword, @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        UserAccount user = current(principal);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            ra.addFlashAttribute("errorMessage", "Your current password is incorrect.");
            return "redirect:/settings";
        }
        if (newPassword == null || newPassword.length() < 8 || newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            ra.addFlashAttribute("errorMessage", "New password must contain at least 8 characters and no more than 72 UTF-8 bytes.");
            return "redirect:/settings";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("errorMessage", "New password and confirmation do not match.");
            return "redirect:/settings";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        users.save(user);
        ra.addFlashAttribute("successMessage", "Password changed successfully. Please use the new password next time you sign in.");
        return "redirect:/settings";
    }

    private UserAccount current(Principal principal) {
        return users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
    }
    private String clean(String value) { return value == null ? null : value.trim(); }
    private String roleLabel(Role role) {
        return switch (role) {
            case ROLE_ADMIN -> "Administrator";
            case ROLE_VENDOR -> "Vendor";
            case ROLE_CUSTOMER -> "Customer";
        };
    }
}
