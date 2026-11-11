package com.asmj.marketplace.controller;

import com.asmj.marketplace.chat.ChatAttachment;
import com.asmj.marketplace.chat.ChatMessage;
import com.asmj.marketplace.chat.VendorApplicationConversation;
import com.asmj.marketplace.chat.VendorApplicationConversationRepository;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VendorApplicationChatController {
    private final UserAccountRepository users;
    private final VendorApplicationConversationRepository conversations;
    private final CloudinaryImageService uploads;

    public VendorApplicationChatController(UserAccountRepository users,
            VendorApplicationConversationRepository conversations, CloudinaryImageService uploads) {
        this.users = users; this.conversations = conversations; this.uploads = uploads;
    }

    @PostMapping("/admin/vendors/{vendorId}/application-chat")
    public String adminReply(@PathVariable String vendorId, @RequestParam(required=false, defaultValue="") String message,
            @RequestParam(required=false) MultipartFile attachment, Principal principal, RedirectAttributes redirect) {
        UserAccount admin = current(principal);
        if (admin.getRole() != Role.ROLE_ADMIN) throw new org.springframework.security.access.AccessDeniedException("Administrator access required.");
        UserAccount vendor = vendor(vendorId);
        return append(vendor, "ADMIN", admin.getFullName(), message, attachment, "/admin/vendors/" + vendorId, redirect);
    }

    @PostMapping("/vendor/application-chat")
    public String vendorReply(@RequestParam(required=false, defaultValue="") String message,
            @RequestParam(required=false) MultipartFile attachment, Principal principal, RedirectAttributes redirect) {
        UserAccount vendor = current(principal);
        if (vendor.getRole() != Role.ROLE_VENDOR) throw new org.springframework.security.access.AccessDeniedException("Vendor access required.");
        return append(vendor, "VENDOR", vendor.getFullName(), message, attachment, "/vendor/pending", redirect);
    }

    private String append(UserAccount vendor, String senderType, String senderName, String message,
            MultipartFile attachment, String redirectTo, RedirectAttributes redirect) {
        String body = message == null ? "" : message.trim();
        boolean hasFile = attachment != null && !attachment.isEmpty();
        if ((body.isEmpty() && !hasFile) || body.length() > 2000) {
            redirect.addFlashAttribute("errorMessage", "Enter a message or attach a file. Messages may contain up to 2000 characters.");
            return "redirect:" + redirectTo;
        }
        VendorApplicationConversation conversation = conversations.findByVendorUserId(vendor.getId())
                .orElseGet(() -> {
                    VendorApplicationConversation created = new VendorApplicationConversation();
                    created.setVendorUserId(vendor.getId());
                    created.setVendorName(vendor.getFullName());
                    created.setVendorEmail(vendor.getEmail());
                    return created;
                });
        ChatMessage entry = new ChatMessage(senderType, senderName == null ? "Marketplace user" : senderName, body);
        if (hasFile) {
            try {
                Map<String,Object> uploaded = uploads.uploadDocument(attachment, "asmj-marketplace/vendor-onboarding");
                entry.getAttachments().add(new ChatAttachment(attachment.getOriginalFilename(),
                        String.valueOf(uploaded.get("secure_url")), attachment.getContentType()));
            } catch (IllegalArgumentException | IllegalStateException ex) {
                redirect.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:" + redirectTo;
            }
        }
        conversation.getMessages().add(entry);
        conversation.setUpdatedAt(Instant.now());
        if ("ADMIN".equals(senderType)) conversation.setAdminLastReadAt(Instant.now());
        else conversation.setVendorLastReadAt(Instant.now());
        conversations.save(conversation);
        redirect.addFlashAttribute("successMessage", "Your message has been sent.");
        return "redirect:" + redirectTo;
    }

    private UserAccount current(Principal principal) {
        if (principal == null) throw new org.springframework.security.access.AccessDeniedException("Sign in required.");
        return users.findByEmailIgnoreCase(principal.getName()).orElseThrow(
                () -> new org.springframework.security.access.AccessDeniedException("Account not found."));
    }
    private UserAccount vendor(String id) {
        UserAccount vendor = users.findById(id).orElseThrow(() -> new IllegalArgumentException("Vendor account not found."));
        if (vendor.getRole() != Role.ROLE_VENDOR) throw new IllegalArgumentException("Selected account is not a vendor.");
        return vendor;
    }
}
