package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_listings")
public class ServiceListing {
    @Id
    private String id;
    private String providerId;
    private String providerName;
    private String categoryId;
    private String categoryName;
    private String subcategoryId;
    private String subcategoryName;
    private String title;
    private String description;
    private String pricingType;
    private BigDecimal price;
    private Integer durationMinutes;
    private String availableDays;
    private String startTime;
    private String endTime;
    private Integer slotIntervalMinutes;
    private String serviceMode;
    private String servicePinCodes;
    private boolean active = true;
    private String status = "PENDING";
    private Instant createdAt = Instant.now();
    private Instant approvedAt;

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String value) { this.providerId = value; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String value) { this.providerName = value; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String value) { this.categoryId = value; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String value) { this.categoryName = value; }
    public String getSubcategoryId() { return subcategoryId; }
    public void setSubcategoryId(String value) { this.subcategoryId = value; }
    public String getSubcategoryName() { return subcategoryName; }
    public void setSubcategoryName(String value) { this.subcategoryName = value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { this.title = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
    public String getPricingType() { return pricingType; }
    public void setPricingType(String value) { this.pricingType = value; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal value) { this.price = value; }
    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String value) { this.availableDays = value; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String value) { this.startTime = value; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String value) { this.endTime = value; }
    public Integer getSlotIntervalMinutes() { return slotIntervalMinutes; }
    public void setSlotIntervalMinutes(Integer value) { this.slotIntervalMinutes = value; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer value) { this.durationMinutes = value; }
    public String getServiceMode() { return serviceMode; }
    public void setServiceMode(String value) { this.serviceMode = value; }
    public String getServicePinCodes() { return servicePinCodes; }
    public void setServicePinCodes(String value) { this.servicePinCodes = value; }
    public boolean getActive() { return active; }
    public void setActive(boolean value) { this.active = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
    public Instant getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Instant value) { this.approvedAt = value; }
}
