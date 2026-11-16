package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_bookings")
public class ServiceBooking {
    @Id
    private String id;
    private String listingId;
    private String providerId;
    private String providerName;
    private String providerUpiId;
    private String customerEmail;
    private String customerName;
    private String customerMobile;
    private String serviceTitle;
    private String serviceAddress;
    private String pinCode;
    private Instant appointmentStart;
    private Instant appointmentEnd;
    private String appointmentDisplay;
    private String customerNotes;
    private BigDecimal quotedAmount;
    private BigDecimal agreedAmount;
    private String status = "PENDING";
    private String providerResponse;
    private Instant createdAt = Instant.now();
    private Instant acceptedAt;
    private Instant completedAt;
    private Instant customerConfirmedAt;

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getListingId() { return listingId; }
    public void setListingId(String value) { this.listingId = value; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String value) { this.providerId = value; }
    public String getProviderUpiId() { return providerUpiId; }
    public void setProviderUpiId(String value) { this.providerUpiId = value; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String value) { this.providerName = value; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String value) { this.customerEmail = value; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String value) { this.customerName = value; }
    public String getCustomerMobile() { return customerMobile; }
    public void setCustomerMobile(String value) { this.customerMobile = value; }
    public String getServiceTitle() { return serviceTitle; }
    public void setServiceTitle(String value) { this.serviceTitle = value; }
    public String getServiceAddress() { return serviceAddress; }
    public void setServiceAddress(String value) { this.serviceAddress = value; }
    public String getPinCode() { return pinCode; }
    public void setPinCode(String value) { this.pinCode = value; }
    public Instant getAppointmentStart() { return appointmentStart; }
    public void setAppointmentStart(Instant value) { this.appointmentStart = value; }
    public String getAppointmentDisplay() { return appointmentDisplay; }
    public void setAppointmentDisplay(String value) { this.appointmentDisplay = value; }
    public Instant getAppointmentEnd() { return appointmentEnd; }
    public void setAppointmentEnd(Instant value) { this.appointmentEnd = value; }
    public String getCustomerNotes() { return customerNotes; }
    public void setCustomerNotes(String value) { this.customerNotes = value; }
    public BigDecimal getQuotedAmount() { return quotedAmount; }
    public void setQuotedAmount(BigDecimal value) { this.quotedAmount = value; }
    public BigDecimal getAgreedAmount() { return agreedAmount; }
    public void setAgreedAmount(BigDecimal value) { this.agreedAmount = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getProviderResponse() { return providerResponse; }
    public void setProviderResponse(String value) { this.providerResponse = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Instant value) { this.acceptedAt = value; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant value) { this.completedAt = value; }
    public Instant getCustomerConfirmedAt() { return customerConfirmedAt; }
    public void setCustomerConfirmedAt(Instant value) { this.customerConfirmedAt = value; }
}
