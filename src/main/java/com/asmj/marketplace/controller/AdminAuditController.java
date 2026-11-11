package com.asmj.marketplace.controller;
import com.asmj.marketplace.audit.AuditEventRepository; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.GetMapping;
@Controller public class AdminAuditController { private final AuditEventRepository events; public AdminAuditController(AuditEventRepository r){events=r;} @GetMapping("/admin/audit") public String audit(Model m){m.addAttribute("events",events.findTop300ByOrderByCreatedAtDesc());return "admin/audit";} }
