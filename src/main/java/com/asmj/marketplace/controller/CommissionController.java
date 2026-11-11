package com.asmj.marketplace.controller;
import com.asmj.marketplace.commission.CommissionService;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.math.BigDecimal;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class CommissionController {
 private final CommissionService commissions; private final UserAccountRepository users; private final CloudinaryImageService uploads;
 public CommissionController(CommissionService commissions,UserAccountRepository users,CloudinaryImageService uploads){this.commissions=commissions;this.users=users;this.uploads=uploads;}
 @GetMapping("/admin/commissions") public String admin(Model model){model.addAttribute("entries",commissions.all());model.addAttribute("rate",commissions.getRatePercent());return "admin/commissions";}
 @PostMapping("/admin/commissions/rate") public String rate(@RequestParam BigDecimal rate,RedirectAttributes ra){try{commissions.setRatePercent(rate);ra.addFlashAttribute("successMessage","Commission rate updated. New completed orders will use this rate.");}catch(IllegalArgumentException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/admin/commissions";}
 @PostMapping("/admin/commissions/{id}/paid") public String paid(@PathVariable String id,@RequestParam(required=false) MultipartFile cashReceipt,Principal principal,RedirectAttributes ra){try{com.asmj.marketplace.commission.CommissionLedger entry=commissions.all().stream().filter(e->e.getId().equals(id)).findFirst().orElseThrow();String url=null,name=null;if("CASH".equals(entry.getPaymentMethod())){if(cashReceipt==null||cashReceipt.isEmpty())throw new IllegalArgumentException("Upload the cash receipt received by admin.");Map<String,Object> f=uploads.uploadDocument(cashReceipt,"asmj-marketplace/commission-cash-receipts");url=String.valueOf(f.get("secure_url"));name=cashReceipt.getOriginalFilename();}commissions.markPaid(id,principal.getName(),url,name);ra.addFlashAttribute("successMessage","Commission payment verified and receipt recorded.");}catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/admin/commissions";}
 @PostMapping("/admin/commissions/{id}/reject") public String reject(@PathVariable String id,@RequestParam(required=false) String note,RedirectAttributes ra){try{commissions.rejectPaymentSubmission(id,"admin",note);ra.addFlashAttribute("successMessage","Commission evidence rejected. Vendor can submit payment details again.");}catch(IllegalArgumentException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/admin/commissions";}
 @PostMapping("/vendor/commissions/{id}/submit") public String submit(@PathVariable String id,@RequestParam String paymentMethod,@RequestParam(required=false) String upiReference,@RequestParam(required=false) MultipartFile paymentProof,Principal principal,RedirectAttributes ra){try{UserAccount u=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();String url=null,name=null;if("UPI".equals(paymentMethod)){if(paymentProof==null||paymentProof.isEmpty())throw new IllegalArgumentException("Upload your UPI payment screenshot.");Map<String,Object> f=uploads.uploadDocument(paymentProof,"asmj-marketplace/commission-upi-proofs");url=String.valueOf(f.get("secure_url"));name=paymentProof.getOriginalFilename();}commissions.submitPayment(id,u.getId(),paymentMethod,upiReference,url,name);ra.addFlashAttribute("successMessage","Commission payment details submitted. Marketplace admin will verify them.");}catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/vendor/commissions";}
 @GetMapping("/vendor/commissions") public String vendor(Principal principal,Model model){UserAccount user=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();model.addAttribute("entries",commissions.forVendor(user.getId()));model.addAttribute("overdue",commissions.hasOverdueCommission(user.getId()));return "vendor/commissions";}
}
