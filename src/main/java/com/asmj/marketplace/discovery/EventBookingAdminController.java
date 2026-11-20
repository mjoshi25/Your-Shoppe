package com.asmj.marketplace.discovery;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.audit.AuditService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
public class EventBookingAdminController {
 private final EventBookingSettingsRepository settings; private final MarketplaceFinancialEntryRepository entries; private final UserAccountRepository users; private final AuditService audit;
 public EventBookingAdminController(EventBookingSettingsRepository s,MarketplaceFinancialEntryRepository e,UserAccountRepository u,AuditService a){settings=s;entries=e;users=u;audit=a;}
 private EventBookingSettings current(){return settings.findById("default").orElseGet(()->settings.save(new EventBookingSettings()));}
 @GetMapping("/admin/event-booking-settings") public String settingsPage(Model m){m.addAttribute("settings",current());return "admin/event-booking-settings";}
 @PostMapping("/admin/event-booking-settings") public String save(@RequestParam(defaultValue="false") boolean enabled,@RequestParam String chargeMode,@RequestParam(defaultValue="0") BigDecimal fixedCharge,@RequestParam(defaultValue="0") BigDecimal percentageCharge,RedirectAttributes ra){
  if(!List.of("FIXED","PERCENTAGE","BOTH").contains(chargeMode)||fixedCharge.signum()<0||percentageCharge.signum()<0||percentageCharge.compareTo(new BigDecimal("100"))>0){ra.addFlashAttribute("errorMessage","Enter a valid charge configuration. Percentage must be between 0 and 100.");return "redirect:/admin/event-booking-settings";}
  EventBookingSettings x=current();x.setEnabled(enabled);x.setChargeMode(chargeMode);x.setFixedCharge(fixedCharge);x.setPercentageCharge(percentageCharge);x.setUpdatedAt(Instant.now());settings.save(x);ra.addFlashAttribute("successMessage","Event booking settings saved. New bookings will use this configuration.");return "redirect:/admin/event-booking-settings";
 }
 @GetMapping("/admin/financial-ledger") public String ledger(Model m){m.addAttribute("entries",entries.findAll());return "admin/financial-ledger";}
 @PostMapping("/admin/financial-ledger/{id}/verify") public String verify(@PathVariable String id,Authentication auth,RedirectAttributes ra){MarketplaceFinancialEntry e=entries.findById(id).orElse(null);if(e==null){ra.addFlashAttribute("errorMessage","Ledger entry not found.");return "redirect:/admin/financial-ledger";}if(!"SUBMITTED_FOR_VERIFICATION".equals(e.getPaymentStatus())){ra.addFlashAttribute("errorMessage","Only submitted remittances can be verified.");return "redirect:/admin/financial-ledger";}e.setPaymentStatus("PAID");e.setSettlementStatus("SETTLED");e.setVerifiedBy(auth.getName());e.setVerifiedAt(Instant.now());e.setPaidAt(Instant.now());entries.save(e);audit.record(auth.getName(),"ADMIN","VERIFY_VENDOR_REMITTANCE","FINANCIAL_ENTRY",id,"Verified vendor remittance reference="+e.getTransactionReference());ra.addFlashAttribute("successMessage","Payment verified and settlement marked complete.");return "redirect:/admin/financial-ledger";}
 @PostMapping("/admin/financial-ledger/{id}/reject") public String reject(@PathVariable String id,@RequestParam String reason,Authentication auth,RedirectAttributes ra){MarketplaceFinancialEntry e=entries.findById(id).orElse(null);if(e==null||!"SUBMITTED_FOR_VERIFICATION".equals(e.getPaymentStatus())){ra.addFlashAttribute("errorMessage","Submitted remittance not found.");return "redirect:/admin/financial-ledger";}e.setPaymentStatus("PAYABLE");e.setSettlementStatus("UNSETTLED");e.setNotes(reason==null?"Rejected by Admin":reason.substring(0,Math.min(300,reason.length())));e.setVerifiedBy(auth.getName());e.setVerifiedAt(Instant.now());entries.save(e);audit.record(auth.getName(),"ADMIN","REJECT_VENDOR_REMITTANCE","FINANCIAL_ENTRY",id,e.getNotes());ra.addFlashAttribute("successMessage","Remittance rejected; amount remains payable.");return "redirect:/admin/financial-ledger";}
}
