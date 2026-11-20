package com.asmj.marketplace.discovery;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Immutable-at-source financial snapshot for marketplace extension bookings. */
@Document(collection = "marketplace_financial_entries")
public class MarketplaceFinancialEntry {
 @Id private String id; private String moduleType; private String referenceId; private String referenceNumber;
 private String payerType; private String payerId; private String payeeType; private String payeeId;
 private String component; private BigDecimal amount=BigDecimal.ZERO; private String currency="INR";
 private String paymentStatus="PAYABLE"; private String settlementStatus="UNSETTLED";
 private String paymentMethod; private String transactionReference; private String proofUrl; private String submittedBy; private String verifiedBy; private String notes; private Instant submittedAt; private Instant verifiedAt; private Instant createdAt=Instant.now(); private Instant paidAt;
 public String getId(){return id;} public void setId(String v){id=v;} public String getModuleType(){return moduleType;} public void setModuleType(String v){moduleType=v;}
 public String getReferenceId(){return referenceId;} public void setReferenceId(String v){referenceId=v;} public String getReferenceNumber(){return referenceNumber;} public void setReferenceNumber(String v){referenceNumber=v;}
 public String getPayerType(){return payerType;} public void setPayerType(String v){payerType=v;} public String getPayerId(){return payerId;} public void setPayerId(String v){payerId=v;}
 public String getPayeeType(){return payeeType;} public void setPayeeType(String v){payeeType=v;} public String getPayeeId(){return payeeId;} public void setPayeeId(String v){payeeId=v;}
 public String getComponent(){return component;} public void setComponent(String v){component=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
 public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public String getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(String v){paymentStatus=v;}
 public String getSettlementStatus(){return settlementStatus;} public void setSettlementStatus(String v){settlementStatus=v;} public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
 public String getTransactionReference(){return transactionReference;} public void setTransactionReference(String v){transactionReference=v;} public String getProofUrl(){return proofUrl;} public void setProofUrl(String v){proofUrl=v;} public String getSubmittedBy(){return submittedBy;} public void setSubmittedBy(String v){submittedBy=v;} public String getVerifiedBy(){return verifiedBy;} public void setVerifiedBy(String v){verifiedBy=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public Instant getSubmittedAt(){return submittedAt;} public void setSubmittedAt(Instant v){submittedAt=v;} public Instant getVerifiedAt(){return verifiedAt;} public void setVerifiedAt(Instant v){verifiedAt=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getPaidAt(){return paidAt;} public void setPaidAt(Instant v){paidAt=v;}
}
