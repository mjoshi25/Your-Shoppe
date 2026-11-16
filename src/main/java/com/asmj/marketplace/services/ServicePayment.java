package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_payments")
public class ServicePayment {
    @Id
    private String id;
    private String bookingId;
    private String providerId;
    private String customerEmail;
    private BigDecimal amount;
    private String method;
    private String utr;
    private String proofUrl;
    private String status = "PENDING";
    private Instant verifiedAt;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String value) { this.bookingId = value; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String value) { this.providerId = value; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String value) { this.customerEmail = value; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal value) { this.amount = value; }
    public String getMethod() { return method; }
    public void setMethod(String value) { this.method = value; }
    public String getUtr() { return utr; }
    public void setUtr(String value) { this.utr = value; }
    public String getProofUrl() { return proofUrl; }
    public void setProofUrl(String value) { this.proofUrl = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public Instant getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Instant value) { this.verifiedAt = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
}
