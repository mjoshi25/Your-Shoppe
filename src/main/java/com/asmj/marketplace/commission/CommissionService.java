package com.asmj.marketplace.commission;

import com.asmj.marketplace.order.MarketplaceOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CommissionService {
 private final CommissionLedgerRepository ledger;
 private final CommissionSettingsRepository settings;
 private CommissionSettings current;
 private static final BigDecimal HUNDRED=new BigDecimal("100");
 public CommissionService(CommissionLedgerRepository ledger,CommissionSettingsRepository settings){this.ledger=ledger;this.settings=settings;this.current=settings.findById("commission").orElseGet(CommissionSettings::new);normalizeLegacySettings();}
 private void normalizeLegacySettings(){if(current.getSlabs().isEmpty())current.setRatePercent(current.getRatePercent().signum()>0?current.getRatePercent():new BigDecimal("5.00"));}
 public BigDecimal getRatePercent(){return current.getRatePercent();}
 public BigDecimal getFixedAmount(){return current.getFixedAmount();}
 public List<CommissionSettings.CommissionSlab> getSlabs(){return current.getSlabs();}
 public void setRatePercent(BigDecimal rate){saveConfiguration(BigDecimal.ZERO,List.of(new CommissionSettings.CommissionSlab(BigDecimal.ZERO,null,rate)));}
 public void saveConfiguration(BigDecimal fixedAmount,List<CommissionSettings.CommissionSlab> input){
  if(fixedAmount==null||fixedAmount.signum()<0||fixedAmount.scale()>2)throw new IllegalArgumentException("Fixed commission must be zero or a positive amount with up to two decimals.");
  if(input==null||input.isEmpty())throw new IllegalArgumentException("Add at least one order amount slab.");
  List<CommissionSettings.CommissionSlab> slabs=new ArrayList<>(input);slabs.sort(Comparator.comparing(CommissionSettings.CommissionSlab::getMinAmount,Comparator.nullsFirst(Comparator.naturalOrder())));
  BigDecimal expected=BigDecimal.ZERO;
  for(int i=0;i<slabs.size();i++){
   CommissionSettings.CommissionSlab s=slabs.get(i);
   if(s.getMinAmount()==null||s.getRatePercent()==null||s.getMinAmount().signum()<0||s.getMinAmount().scale()>2||s.getRatePercent().signum()<0||s.getRatePercent().compareTo(HUNDRED)>0||s.getRatePercent().scale()>2)throw new IllegalArgumentException("Enter valid slab minimums and percentage rates (0–100%).");
   if(s.getMinAmount().compareTo(expected)!=0)throw new IllegalArgumentException("Order amount slabs must be continuous, start at ₹0 and have no gaps or overlaps.");
   if(i<slabs.size()-1){if(s.getMaxAmount()==null||s.getMaxAmount().scale()>2||s.getMaxAmount().compareTo(s.getMinAmount())<=0)throw new IllegalArgumentException("Every slab except the last needs a maximum amount greater than its minimum.");expected=s.getMaxAmount().add(new BigDecimal("0.01"));}
   else if(s.getMaxAmount()!=null)throw new IllegalArgumentException("The final slab must have a blank maximum to cover all higher order amounts.");
  }
  current.setId("commission");current.setFixedAmount(fixedAmount.setScale(2,RoundingMode.HALF_UP));current.setSlabs(slabs);settings.save(current);
 }
 private CommissionSettings.CommissionSlab slabFor(BigDecimal amount){return current.getSlabs().stream().filter(s->amount.compareTo(s.getMinAmount())>=0&&(s.getMaxAmount()==null||amount.compareTo(s.getMaxAmount())<=0)).findFirst().orElseThrow(()->new IllegalStateException("No commission slab matches this order amount."));}
 public void recordCompletedOrder(MarketplaceOrder order){
  if(order==null||order.getId()==null||order.getVendorUserId()==null||order.getSubtotal()==null)return;
  if(ledger.findByOrderId(order.getId()).isPresent())return;
  java.time.Instant completion=order.getCompletedAt()!=null?order.getCompletedAt():order.getUpdatedAt();
  LocalDate date=completion==null?LocalDate.now(ZoneId.of("Asia/Kolkata")):completion.atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
  LocalDate start=date.with(DayOfWeek.MONDAY);
  BigDecimal eligible=order.getSubtotal().max(BigDecimal.ZERO);
  CommissionSettings.CommissionSlab slab=slabFor(eligible);
  BigDecimal fixed=current.getFixedAmount();
  BigDecimal percentage=eligible.multiply(slab.getRatePercent()).divide(HUNDRED,2,RoundingMode.HALF_UP);
  CommissionLedger entry=new CommissionLedger();entry.setOrderId(order.getId());entry.setOrderNumber(order.getOrderNumber());entry.setVendorUserId(order.getVendorUserId());entry.setVendorName(order.getVendorName());entry.setWeekStart(start);entry.setWeekEnd(start.plusDays(6));entry.setEligibleAmount(eligible);entry.setFixedAmount(fixed);entry.setSlabMinAmount(slab.getMinAmount());entry.setSlabMaxAmount(slab.getMaxAmount());entry.setRatePercent(slab.getRatePercent());entry.setPercentageCommissionAmount(percentage);entry.setCommissionAmount(fixed.add(percentage).setScale(2,RoundingMode.HALF_UP));ledger.save(entry);
 }
 public boolean hasOverdueCommission(String vendorId){LocalDate thisWeek=LocalDate.now(ZoneId.of("Asia/Kolkata")).with(DayOfWeek.MONDAY);return !ledger.findByVendorUserIdAndStatusAndWeekEndBefore(vendorId,"UNPAID",thisWeek).isEmpty();}
 public List<CommissionLedger> forVendor(String vendorId){return ledger.findByVendorUserIdOrderByWeekStartDesc(vendorId);}
 public List<CommissionLedger> all(){return ledger.findAllByOrderByWeekStartDesc();}
 public void submitPayment(String id,String vendorId,String method,String utr,String proofUrl,String proofName){CommissionLedger entry=ledger.findById(id).orElseThrow();if(!vendorId.equals(entry.getVendorUserId()))throw new IllegalArgumentException("You cannot update another vendor's commission.");if(!"UNPAID".equals(entry.getStatus()))throw new IllegalArgumentException("This commission entry is already settled.");if(!"UPI".equals(method)&&!"CASH".equals(method))throw new IllegalArgumentException("Choose UPI or cash payment.");if("UPI".equals(method)&&(utr==null||utr.isBlank()||proofUrl==null||proofUrl.isBlank()))throw new IllegalArgumentException("UPI payment requires a UTR and screenshot.");entry.setPaymentMethod(method);entry.setUpiReference("UPI".equals(method)?utr.trim():null);entry.setPaymentProofUrl("UPI".equals(method)?proofUrl:null);entry.setPaymentProofFileName("UPI".equals(method)?proofName:null);entry.setSubmittedAt(java.time.Instant.now());entry.setVerificationNote("CASH".equals(method)?"Vendor reported cash payment; admin must confirm receipt.":"UPI proof submitted; awaiting admin verification.");ledger.save(entry);}
 public void rejectPaymentSubmission(String id,String admin,String note){CommissionLedger entry=ledger.findById(id).orElseThrow();if(!"UNPAID".equals(entry.getStatus())||entry.getPaymentMethod()==null)throw new IllegalArgumentException("There is no pending commission payment submission to reject.");entry.setPaymentMethod(null);entry.setUpiReference(null);entry.setPaymentProofUrl(null);entry.setPaymentProofFileName(null);entry.setCashReceiptUrl(null);entry.setCashReceiptFileName(null);entry.setSubmittedAt(null);entry.setVerificationNote("Payment submission rejected by admin: "+(note==null||note.isBlank()?"Please submit again.":note.trim()));ledger.save(entry);}
 public void markPaid(String id,String admin,String cashReceiptUrl,String cashReceiptName){CommissionLedger entry=ledger.findById(id).orElseThrow();if(!"UNPAID".equals(entry.getStatus()))throw new IllegalArgumentException("This commission entry is already settled.");if("UPI".equals(entry.getPaymentMethod())&&(entry.getUpiReference()==null||entry.getPaymentProofUrl()==null))throw new IllegalArgumentException("Vendor must submit UPI reference and screenshot first.");if("CASH".equals(entry.getPaymentMethod())){if(cashReceiptUrl==null||cashReceiptUrl.isBlank())throw new IllegalArgumentException("Upload the cash receipt after receiving payment.");entry.setCashReceiptUrl(cashReceiptUrl);entry.setCashReceiptFileName(cashReceiptName);}if(entry.getPaymentMethod()==null)throw new IllegalArgumentException("Vendor must first submit a payment method.");entry.setStatus("PAID");entry.setPaidAt(java.time.Instant.now());entry.setPaidBy(admin);entry.setVerifiedAt(java.time.Instant.now());entry.setVerificationNote("Payment verified by admin.");ledger.save(entry);}
}
