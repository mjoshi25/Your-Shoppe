package com.asmj.marketplace.commission;

import com.asmj.marketplace.order.MarketplaceOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class CommissionService {
 private final CommissionLedgerRepository ledger; private final CommissionSettingsRepository settings;
 private BigDecimal ratePercent=new BigDecimal("5.00");
 public CommissionService(CommissionLedgerRepository ledger,CommissionSettingsRepository settings){this.ledger=ledger;this.settings=settings; this.ratePercent=settings.findById("commission").map(CommissionSettings::getRatePercent).orElse(new BigDecimal("5.00"));}
 public BigDecimal getRatePercent(){return ratePercent;}
 public void setRatePercent(BigDecimal rate){if(rate==null||rate.compareTo(BigDecimal.ZERO)<0||rate.compareTo(new BigDecimal("100"))>0)throw new IllegalArgumentException("Commission rate must be between 0 and 100 percent.");this.ratePercent=rate; CommissionSettings saved=settings.findById("commission").orElseGet(CommissionSettings::new);saved.setId("commission");saved.setRatePercent(rate);settings.save(saved);}
 public void recordCompletedOrder(MarketplaceOrder order){
  if(order==null||order.getId()==null||order.getVendorUserId()==null||order.getSubtotal()==null)return;
  if(ledger.findByOrderId(order.getId()).isPresent())return;
  java.time.Instant completion=order.getCompletedAt()!=null?order.getCompletedAt():order.getUpdatedAt();
  LocalDate date=(completion==null?LocalDate.now(ZoneId.of("Asia/Kolkata")):completion.atZone(ZoneId.of("Asia/Kolkata")).toLocalDate());
  LocalDate start=date.with(DayOfWeek.MONDAY), end=start.plusDays(6);
  BigDecimal rate=order.getCommissionRatePercent()!=null?order.getCommissionRatePercent():(ratePercent==null?new BigDecimal("5.00"):ratePercent);
  CommissionLedger entry=new CommissionLedger();entry.setOrderId(order.getId());entry.setOrderNumber(order.getOrderNumber());entry.setVendorUserId(order.getVendorUserId());entry.setVendorName(order.getVendorName());entry.setWeekStart(start);entry.setWeekEnd(end);entry.setEligibleAmount(order.getSubtotal());entry.setRatePercent(rate);entry.setCommissionAmount(order.getSubtotal().multiply(rate).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP));entry.setStatus("UNPAID");ledger.save(entry);
 }
 public boolean hasOverdueCommission(String vendorId){LocalDate thisWeek=LocalDate.now(ZoneId.of("Asia/Kolkata")).with(DayOfWeek.MONDAY);return !ledger.findByVendorUserIdAndStatusAndWeekEndBefore(vendorId,"UNPAID",thisWeek).isEmpty();}
 public java.util.List<CommissionLedger> forVendor(String vendorId){return ledger.findByVendorUserIdOrderByWeekStartDesc(vendorId);}
 public java.util.List<CommissionLedger> all(){return ledger.findAllByOrderByWeekStartDesc();}
 public void submitPayment(String id,String vendorId,String method,String utr,String proofUrl,String proofName){CommissionLedger entry=ledger.findById(id).orElseThrow();if(!vendorId.equals(entry.getVendorUserId()))throw new IllegalArgumentException("You cannot update another vendor's commission.");if(!"UNPAID".equals(entry.getStatus()))throw new IllegalArgumentException("This commission entry is already settled.");if(!"UPI".equals(method)&&!"CASH".equals(method))throw new IllegalArgumentException("Choose UPI or cash payment.");if("UPI".equals(method)&&(utr==null||utr.isBlank()||proofUrl==null||proofUrl.isBlank()))throw new IllegalArgumentException("UPI payment requires a UTR and screenshot.");entry.setPaymentMethod(method);entry.setUpiReference("UPI".equals(method)?utr.trim():null);entry.setPaymentProofUrl("UPI".equals(method)?proofUrl:null);entry.setPaymentProofFileName("UPI".equals(method)?proofName:null);entry.setSubmittedAt(java.time.Instant.now());entry.setVerificationNote("CASH".equals(method)?"Vendor reported cash payment; admin must confirm receipt.":"UPI proof submitted; awaiting admin verification.");ledger.save(entry);}
 public void rejectPaymentSubmission(String id,String admin,String note){CommissionLedger entry=ledger.findById(id).orElseThrow();if(!"UNPAID".equals(entry.getStatus())||entry.getPaymentMethod()==null)throw new IllegalArgumentException("There is no pending commission payment submission to reject.");entry.setPaymentMethod(null);entry.setUpiReference(null);entry.setPaymentProofUrl(null);entry.setPaymentProofFileName(null);entry.setCashReceiptUrl(null);entry.setCashReceiptFileName(null);entry.setSubmittedAt(null);entry.setVerificationNote("Payment submission rejected by admin: "+(note==null||note.isBlank()?"Please submit again.":note.trim()));ledger.save(entry);}
 public void markPaid(String id,String admin,String cashReceiptUrl,String cashReceiptName){CommissionLedger entry=ledger.findById(id).orElseThrow();if(!"UNPAID".equals(entry.getStatus()))throw new IllegalArgumentException("This commission entry is already settled.");if("UPI".equals(entry.getPaymentMethod())&&(entry.getUpiReference()==null||entry.getPaymentProofUrl()==null))throw new IllegalArgumentException("Vendor must submit UPI reference and screenshot first.");if("CASH".equals(entry.getPaymentMethod())){if(cashReceiptUrl==null||cashReceiptUrl.isBlank())throw new IllegalArgumentException("Upload the cash receipt after receiving payment.");entry.setCashReceiptUrl(cashReceiptUrl);entry.setCashReceiptFileName(cashReceiptName);}if(entry.getPaymentMethod()==null)throw new IllegalArgumentException("Vendor must first submit a payment method.");entry.setStatus("PAID");entry.setPaidAt(java.time.Instant.now());entry.setPaidBy(admin);entry.setVerifiedAt(java.time.Instant.now());entry.setVerificationNote("Payment verified by admin.");ledger.save(entry);}
}
