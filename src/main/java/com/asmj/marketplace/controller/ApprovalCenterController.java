package com.asmj.marketplace.controller;

import com.asmj.marketplace.approval.*;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ApprovalCenterController {
 private final ApprovalCaseRepository cases; private final UserAccountRepository users; private final CloudinaryImageService uploads;
 public ApprovalCenterController(ApprovalCaseRepository cases,UserAccountRepository users,CloudinaryImageService uploads){this.cases=cases;this.users=users;this.uploads=uploads;}
 private UserAccount current(Principal p){return users.findByEmailIgnoreCase(p.getName()).orElseThrow();}
 private boolean admin(UserAccount u){return u.getRole()==Role.ROLE_ADMIN;}
 @GetMapping("/admin/approval-center") public String adminQueue(Model m){m.addAttribute("cases",cases.findTop300ByOrderByUpdatedAtDesc());return "admin/approval-center";}
 @GetMapping("/admin/approval-center/new") public String newCase(Model m){m.addAttribute("users",users.findAll());return "admin/approval-case-new";}
 @PostMapping("/admin/approval-center/new") public String create(@RequestParam String ownerEmail,@RequestParam String module,@RequestParam String subjectType,@RequestParam String subjectId,@RequestParam String subjectTitle,@RequestParam String message,Principal p,RedirectAttributes ra){
  UserAccount actor=current(p);if(!admin(actor))return "auth/access-denied"; UserAccount owner=users.findByEmailIgnoreCase(ownerEmail.trim()).orElse(null);
  if(owner==null||owner.getRole()==Role.ROLE_ADMIN||module.isBlank()||subjectType.isBlank()||subjectId.isBlank()||subjectTitle.isBlank()||message.isBlank()){ra.addFlashAttribute("errorMessage","Enter a valid submitter, module, item reference, title and information request.");return "redirect:/admin/approval-center/new";}
  ApprovalCase c=new ApprovalCase();c.setOwnerUserId(owner.getId());c.setOwnerEmail(owner.getEmail());c.setModule(module.trim().toUpperCase());c.setSubjectType(subjectType.trim());c.setSubjectId(subjectId.trim());c.setSubjectTitle(subjectTitle.trim());c.setStatus("ACTION_REQUIRED");c.setLatestAdminMessage(message.trim());c.getHistory().add(new ApprovalEntry(actor.getEmail(),"ADMIN","INFORMATION_REQUESTED",message.trim()));c.setUpdatedAt(Instant.now());cases.save(c);ra.addFlashAttribute("successMessage","Information request sent to "+owner.getEmail()+".");return "redirect:/admin/approval-center/"+c.getId();
 }
 @GetMapping("/admin/approval-center/{id}") public String adminDetail(@PathVariable String id,Model m,Principal p){if(!admin(current(p)))return "auth/access-denied";ApprovalCase c=cases.findById(id).orElseThrow();m.addAttribute("approvalCase",c);return "admin/approval-case-detail";}
 @PostMapping("/admin/approval-center/{id}/request") public String request(@PathVariable String id,@RequestParam String message,@RequestParam(required=false) List<MultipartFile> files,Principal p,RedirectAttributes ra){UserAccount actor=current(p);if(!admin(actor))return "auth/access-denied";ApprovalCase c=cases.findById(id).orElseThrow();if(message==null||message.isBlank()){ra.addFlashAttribute("errorMessage","Enter the details required from the submitter.");return "redirect:/admin/approval-center/"+id;}ApprovalEntry e=new ApprovalEntry(actor.getEmail(),"ADMIN","INFORMATION_REQUESTED",message.trim());addFiles(files,e,c,actor.getEmail());c.getHistory().add(e);c.setLatestAdminMessage(message.trim());c.setStatus("ACTION_REQUIRED");c.setUpdatedAt(Instant.now());c.setDecidedAt(null);c.setDecidedBy(null);cases.save(c);ra.addFlashAttribute("successMessage","Additional information requested.");return "redirect:/admin/approval-center/"+id;}
 @PostMapping("/admin/approval-center/{id}/decision") public String decision(@PathVariable String id,@RequestParam String decision,@RequestParam(required=false) String message,Principal p,RedirectAttributes ra){UserAccount actor=current(p);if(!admin(actor))return "auth/access-denied";ApprovalCase c=cases.findById(id).orElseThrow();String d=decision.toUpperCase();if(!List.of("APPROVED","REJECTED").contains(d)){ra.addFlashAttribute("errorMessage","Invalid decision.");return "redirect:/admin/approval-center/"+id;}c.setStatus(d);c.setDecidedAt(Instant.now());c.setDecidedBy(actor.getEmail());c.setUpdatedAt(Instant.now());c.getHistory().add(new ApprovalEntry(actor.getEmail(),"ADMIN",d,message==null?"":message.trim()));cases.save(c);ra.addFlashAttribute("successMessage","Approval case marked "+d.toLowerCase()+".");return "redirect:/admin/approval-center/"+id;}
 @GetMapping("/my-approval-requests") public String myRequests(Principal p,Model m){UserAccount u=current(p);m.addAttribute("cases",cases.findByOwnerUserIdOrderByUpdatedAtDesc(u.getId()));return "approval/my-requests";}
 @GetMapping("/my-approval-requests/{id}") public String myDetail(@PathVariable String id,Principal p,Model m){UserAccount u=current(p);ApprovalCase c=cases.findById(id).filter(x->u.getId().equals(x.getOwnerUserId())).orElseThrow();m.addAttribute("approvalCase",c);return "approval/request-detail";}
 @PostMapping("/my-approval-requests/{id}/respond") public String respond(@PathVariable String id,@RequestParam(required=false) String message,@RequestParam(required=false) List<MultipartFile> files,Principal p,RedirectAttributes ra){UserAccount u=current(p);ApprovalCase c=cases.findById(id).filter(x->u.getId().equals(x.getOwnerUserId())).orElseThrow();if(!"ACTION_REQUIRED".equals(c.getStatus())){ra.addFlashAttribute("errorMessage","This request is not currently awaiting your response.");return "redirect:/my-approval-requests/"+id;}if((message==null||message.isBlank())&&(files==null||files.stream().allMatch(f->f==null||f.isEmpty()))){ra.addFlashAttribute("errorMessage","Enter a response or attach at least one file.");return "redirect:/my-approval-requests/"+id;}ApprovalEntry e=new ApprovalEntry(u.getEmail(),u.getRole().name(),"SUBMITTED_FOR_REVIEW",message==null?"":message.trim());try{addFiles(files,e,c,u.getEmail());}catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());return "redirect:/my-approval-requests/"+id;}c.getHistory().add(e);c.setStatus("SUBMITTED");c.setSubmittedAt(Instant.now());c.setUpdatedAt(Instant.now());cases.save(c);ra.addFlashAttribute("successMessage","Your response has been submitted for admin review.");return "redirect:/my-approval-requests/"+id;}
 private void addFiles(List<MultipartFile> files,ApprovalEntry entry,ApprovalCase c,String by){if(files==null)return;for(MultipartFile f:files){if(f==null||f.isEmpty())continue;Map<String,Object> result=uploads.uploadDocument(f,"asmj-marketplace/approval-documents");ApprovalAttachment a=new ApprovalAttachment(f.getOriginalFilename(),String.valueOf(result.get("secure_url")),f.getContentType(),by);entry.getAttachments().add(a);c.getAttachments().add(a);}}
}
