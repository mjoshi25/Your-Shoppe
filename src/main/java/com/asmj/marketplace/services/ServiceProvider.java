package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_providers")
public class ServiceProvider {
    @Id
    private String id;
    private String email;
    private String providerType;
    private String displayName;
    private String contactName;
    private String mobile;
    private String upiId;
    private String businessAddress;
    private String city;
    private String state;
    private String pinCodes;
    private Integer experienceYears;
    private String qualifications;
    private String registrationNumber;
    private String documentUrl;
    private String status = "PENDING";
    private String adminNote;
    private Instant createdAt = Instant.now();
    private Instant approvedAt;

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String value) { this.providerType = value; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String value) { this.displayName = value; }
    public String getContactName() { return contactName; }
    public void setContactName(String value) { this.contactName = value; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String value) { this.upiId = value; }
    public String getMobile() { return mobile; }
    public void setMobile(String value) { this.mobile = value; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String value) { this.businessAddress = value; }
    public String getCity() { return city; }
    public void setCity(String value) { this.city = value; }
    public String getState() { return state; }
    public void setState(String value) { this.state = value; }
    public String getPinCodes() { return pinCodes; }
    public void setPinCodes(String value) { this.pinCodes = value; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer value) { this.experienceYears = value; }
    public String getQualifications() { return qualifications; }
    public void setQualifications(String value) { this.qualifications = value; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String value) { this.registrationNumber = value; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String value) { this.documentUrl = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String value) { this.adminNote = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant value) { this.approvedAt = value; }
}
