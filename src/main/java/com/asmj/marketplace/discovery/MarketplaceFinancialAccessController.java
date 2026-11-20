package com.asmj.marketplace.discovery;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.audit.AuditService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MarketplaceFinancialAccessController {
 private final MarketplaceFinancialEntryRepository entries; private final UserAccountRepository users; private final MarketplaceExtensionRequestRepository requests; private final MarketplaceExtensionListingRepository listings; private final AuditService audit;
 public MarketplaceFinancialAccessController(MarketplaceFinancialEntryRepository e,UserAccountRepository u,MarketplaceExtensionRequestRepository r,MarketplaceExtensionListingRepository l,AuditService a){entries=e;users=u;requests=r;listings=l;audit=a;}
 private UserAccount current(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow();}
 @GetMapping("/vendor/financial-ledger") public String vendorLedger(Authentication a,Model m){UserAccount u=current(a);m.addAttribute("entries",entries.findByPayerIdOrderByCreatedAtDesc(u.getId()));return "vendor/financial-ledger";}
 @PostMapping("/vendor/financial-ledger/{id}/submit-payment") public String submit(@PathVariable String id,@RequestParam String transactionReference,@RequestParam(required=false)String paymentMethod,@RequestParam(required=false)String proofUrl,Authentication a,RedirectAttributes ra){UserAccount u=current(a);MarketplaceFinancialEntry e=entries.findById(id).orElse(null);if(e==null||!u.getId().equals(e.getPayerId()))return "auth/access-denied";if(!"PAYABLE".equals(e.getPaymentStatus())||transactionReference==null||transactionReference.isBlank()||transactionReference.length()>100){ra.addFlashAttribute("error","Enter a valid UTR/reference for an unpaid amount.");return "redirect:/vendor/financial-ledger";}e.setTransactionReference(transactionReference.trim());e.setPaymentMethod(paymentMethod==null?"UPI":paymentMethod.trim());e.setProofUrl(proofUrl==null?null:proofUrl.trim());e.setSubmittedBy(a.getName());e.setSubmittedAt(Instant.now());e.setPaymentStatus("SUBMITTED_FOR_VERIFICATION");e.setSettlementStatus("PENDING_VERIFICATION");entries.save(e);audit.record(a.getName(),"VENDOR","SUBMIT_VENDOR_REMITTANCE","FINANCIAL_ENTRY",id,"Vendor submitted payment reference="+e.getTransactionReference());ra.addFlashAttribute("success","Payment submitted for Admin verification. It is not marked paid yet.");return "redirect:/vendor/financial-ledger";}
 @GetMapping("/marketplace-requests/{id}/invoice") public String eventInvoice(@PathVariable String id,Authentication a,Model m){UserAccount u=current(a);MarketplaceExtensionRequest r=requests.findById(id).orElse(null);if(r==null)return "auth/access-denied";MarketplaceExtensionListing l=listings.findById(r.getListingId()).orElse(null);if(l==null)return "auth/access-denied";boolean allowed=u.getRole()==Role.ROLE_ADMIN||(u.getRole()==Role.ROLE_CUSTOMER&&u.getId().equals(r.getRequesterUserId()))||(u.getRole()==Role.ROLE_VENDOR&&u.getId().equals(l.getOwnerUserId()));if(!allowed)return "auth/access-denied";m.addAttribute("request",r);m.addAttribute("listing",l);m.addAttribute("invoiceLabel",("CONFIRMED".equals(r.getStatus())?"BOOKING CONFIRMATION / PAYMENT ADVICE":"BOOKING REQUEST / PROVISIONAL STATEMENT"));return "extensions/event-invoice";}
}
