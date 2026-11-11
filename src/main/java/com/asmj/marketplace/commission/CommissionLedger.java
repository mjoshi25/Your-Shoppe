package com.asmj.marketplace.commission;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="commission_ledger")
@CompoundIndex(name="vendor_week_status_idx",def="{'vendorUserId':1,'weekStart':1,'status':1}")
public class CommissionLedger {
 @Id private String id;
 @Indexed(unique=true) private String orderId;
 private String orderNumber;
 @Indexed private String vendorUserId;
 private String vendorName;
 private LocalDate weekStart;
 private LocalDate weekEnd;
 private BigDecimal eligibleAmount=BigDecimal.ZERO;
 private BigDecimal ratePercent=BigDecimal.ZERO;
 private BigDecimal commissionAmount=BigDecimal.ZERO;
 private String status="UNPAID";
 private Instant createdAt=Instant.now();
 private Instant paidAt;
 private String paidBy;
 private String paymentMethod;
 private String upiReference;
 private String paymentProofUrl;
 private String paymentProofFileName;
 private String cashReceiptUrl;
 private String cashReceiptFileName;
 private String verificationNote;
 private Instant submittedAt;
 private Instant verifiedAt;
 public String getId(){return id;} public void setId(String v){id=v;}
 public String getOrderId(){return orderId;} public void setOrderId(String v){orderId=v;}
 public String getOrderNumber(){return orderNumber;} public void setOrderNumber(String v){orderNumber=v;}
 public String getVendorUserId(){return vendorUserId;} public void setVendorUserId(String v){vendorUserId=v;}
 public String getVendorName(){return vendorName;} public void setVendorName(String v){vendorName=v;}
 public LocalDate getWeekStart(){return weekStart;} public void setWeekStart(LocalDate v){weekStart=v;}
 public LocalDate getWeekEnd(){return weekEnd;} public void setWeekEnd(LocalDate v){weekEnd=v;}
 public BigDecimal getEligibleAmount(){return eligibleAmount;} public void setEligibleAmount(BigDecimal v){eligibleAmount=v;}
 public BigDecimal getRatePercent(){return ratePercent;} public void setRatePercent(BigDecimal v){ratePercent=v;}
 public BigDecimal getCommissionAmount(){return commissionAmount;} public void setCommissionAmount(BigDecimal v){commissionAmount=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
 public Instant getPaidAt(){return paidAt;} public void setPaidAt(Instant v){paidAt=v;}
 public String getPaidBy(){return paidBy;} public void setPaidBy(String v){paidBy=v;}
 public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
 public String getUpiReference(){return upiReference;} public void setUpiReference(String v){upiReference=v;}
 public String getPaymentProofUrl(){return paymentProofUrl;} public void setPaymentProofUrl(String v){paymentProofUrl=v;}
 public String getPaymentProofFileName(){return paymentProofFileName;} public void setPaymentProofFileName(String v){paymentProofFileName=v;}
 public String getCashReceiptUrl(){return cashReceiptUrl;} public void setCashReceiptUrl(String v){cashReceiptUrl=v;}
 public String getCashReceiptFileName(){return cashReceiptFileName;} public void setCashReceiptFileName(String v){cashReceiptFileName=v;}
 public String getVerificationNote(){return verificationNote;} public void setVerificationNote(String v){verificationNote=v;}
 public Instant getSubmittedAt(){return submittedAt;} public void setSubmittedAt(Instant v){submittedAt=v;}
 public Instant getVerifiedAt(){return verifiedAt;} public void setVerifiedAt(Instant v){verifiedAt=v;}
}
