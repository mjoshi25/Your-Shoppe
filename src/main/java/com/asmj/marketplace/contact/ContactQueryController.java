package com.asmj.marketplace.contact;

import com.asmj.marketplace.service.CloudinaryImageService;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactQueryController {
    private final ContactQueryRepository queries;
    private final CloudinaryImageService uploads;
    public ContactQueryController(ContactQueryRepository queries, CloudinaryImageService uploads){this.queries=queries;this.uploads=uploads;}

    @PostMapping("/contact-queries")
    public String submit(@RequestParam String name, @RequestParam String email,
            @RequestParam String mobile, @RequestParam String subject,
            @RequestParam String message, @RequestParam(required=false) MultipartFile attachment,
            @RequestParam(required=false, name="website") String website, RedirectAttributes ra) {
        // Quiet honeypot: bots filling the hidden field receive the same confirmation.
        if(website!=null&&!website.isBlank()){ra.addFlashAttribute("successMessage","Thank you. Your query has been submitted.");return "redirect:/#contact-query";}
        try {
            String n=name==null?"":name.trim(), e=email==null?"":email.trim(), m=mobile==null?"":mobile.trim(), s=subject==null?"":subject.trim(), body=message==null?"":message.trim();
            if(n.length()<2||n.length()>100)throw new IllegalArgumentException("Enter your name (2–100 characters).");
            if(!e.matches("(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$")||e.length()>160)throw new IllegalArgumentException("Enter a valid email address.");
            if(!m.matches("[+0-9()\\- ]{7,20}"))throw new IllegalArgumentException("Enter a valid contact number.");
            if(s.length()<3||s.length()>120)throw new IllegalArgumentException("Subject must contain 3–120 characters.");
            if(body.length()<10||body.length()>3000)throw new IllegalArgumentException("Query must contain 10–3000 characters.");
            ContactQuery q=new ContactQuery();q.setName(n);q.setEmail(e);q.setMobile(m);q.setSubject(s);q.setMessage(body);q.setCreatedAt(Instant.now());
            if(attachment!=null&&!attachment.isEmpty()){
                Map<String,Object> uploaded=uploads.uploadDocument(attachment,"asmj-marketplace/contact-queries");
                q.setAttachmentUrl(String.valueOf(uploaded.get("secure_url")));q.setAttachmentName(attachment.getOriginalFilename());
            }
            queries.save(q);ra.addFlashAttribute("successMessage","Thank you. Your query has been submitted successfully. Our team will review it.");
        } catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());}
        return "redirect:/#contact-query";
    }

    @GetMapping("/admin/contact-queries")
    public String adminQueries(Model model){model.addAttribute("queries",queries.findAllByOrderByCreatedAtDesc());return "admin/contact-queries";}
}
