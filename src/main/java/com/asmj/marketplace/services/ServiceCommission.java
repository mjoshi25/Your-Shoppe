package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_commissions")
public class ServiceCommission {
    @Id
    private String id;
    private String bookingId;
    private String providerId;
    private BigDecimal grossAmount;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String status = "PENDING";
    private Instant createdAt = Instant.now();
    private Instant paidAt;

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String value) { this.bookingId = value; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String value) { this.providerId = value; }
    public BigDecimal getGrossAmount() { return grossAmount; }
    public void setGrossAmount(BigDecimal value) { this.grossAmount = value; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal value) { this.commissionRate = value; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal value) { this.commissionAmount = value; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate value) { this.periodStart = value; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate value) { this.periodEnd = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant value) { this.paidAt = value; }
}
