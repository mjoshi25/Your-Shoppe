package com.asmj.marketplace.order;

import java.math.BigDecimal;
import java.time.Instant;

/** Immutable-in-practice audit row embedded in an order for customer money received or refunded. */
public class PaymentTransaction {
    private String type; // PAYMENT or REFUND
    private String category; // MERCHANDISE, DELIVERY, OTHER for refunds
    private String method;
    private BigDecimal amount = BigDecimal.ZERO;
    private String reference;
    private String note;
    private String recordedBy;
    private String proofUrl;
    private String proofFileName;
    private Instant recordedAt = Instant.now();

    public String getType(){return type;} public void setType(String v){type=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getMethod(){return method;} public void setMethod(String v){method=v;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public String getReference(){return reference;} public void setReference(String v){reference=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public String getRecordedBy(){return recordedBy;} public void setRecordedBy(String v){recordedBy=v;}
    public String getProofUrl(){return proofUrl;} public void setProofUrl(String v){proofUrl=v;}
    public String getProofFileName(){return proofFileName;} public void setProofFileName(String v){proofFileName=v;}
    public Instant getRecordedAt(){return recordedAt;} public void setRecordedAt(Instant v){recordedAt=v;}
}
