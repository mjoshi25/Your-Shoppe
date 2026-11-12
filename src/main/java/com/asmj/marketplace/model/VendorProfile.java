package com.asmj.marketplace.model;

import java.time.Instant;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vendor_profiles")
public class VendorProfile {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private String businessName;
    private String businessDescription;
    private String addressLine;
    private String city;
    private String state;
    private String pinCode;
    private Double shopLatitude;
    private Double shopLongitude;
    private boolean homeDelivery = true;
    private boolean pickupFromShop = true;
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
    private boolean distanceBasedDeliveryEnabled = false;
    private BigDecimal freeDeliveryDistanceKm = new BigDecimal("5.0");
    private BigDecimal extraDeliveryRatePerKm = new BigDecimal("10.00");
    private String upiId;
    private java.util.List<String> serviceablePinCodes = new java.util.ArrayList<>();
    private java.util.List<String> deliverySlotDefinitions = new java.util.ArrayList<>();
    private java.util.List<String> pickupSlotDefinitions = new java.util.ArrayList<>();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getBusinessDescription() { return businessDescription; }
    public void setBusinessDescription(String businessDescription) { this.businessDescription = businessDescription; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public boolean isHomeDelivery() { return homeDelivery; }
    public void setHomeDelivery(boolean value) { this.homeDelivery = value; }
    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal value) { this.deliveryCharge = value; }
    public boolean isDistanceBasedDeliveryEnabled() { return distanceBasedDeliveryEnabled; }
    public void setDistanceBasedDeliveryEnabled(boolean value) { this.distanceBasedDeliveryEnabled = value; }
    public BigDecimal getFreeDeliveryDistanceKm() { return freeDeliveryDistanceKm == null ? new BigDecimal("5.0") : freeDeliveryDistanceKm; }
    public void setFreeDeliveryDistanceKm(BigDecimal value) { this.freeDeliveryDistanceKm = value; }
    public BigDecimal getExtraDeliveryRatePerKm() { return extraDeliveryRatePerKm == null ? new BigDecimal("10.00") : extraDeliveryRatePerKm; }
    public void setExtraDeliveryRatePerKm(BigDecimal value) { this.extraDeliveryRatePerKm = value; }
    public java.util.List<String> getServiceablePinCodes() { return serviceablePinCodes == null ? java.util.List.of() : serviceablePinCodes; }
    public void setServiceablePinCodes(java.util.List<String> v) { serviceablePinCodes = v == null ? new java.util.ArrayList<>() : v; }
    /**
     * Default everyday schedule used for new and legacy vendor profiles that do not
     * yet have their own schedule. Format: DAY|start|end|capacity.
     */
    private static java.util.List<String> defaultDailySlots() {
        java.util.List<String> slots = new java.util.ArrayList<>();
        for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
            slots.add(day.name() + "|09:00|12:00|10");
            slots.add(day.name() + "|12:00|15:00|10");
            slots.add(day.name() + "|15:00|18:00|10");
        }
        return slots;
    }
    public String getDeliverySlotsText() { return String.join("\n", getDeliverySlotDefinitions()); }
    public String getPickupSlotsText() { return String.join("\n", getPickupSlotDefinitions()); }
    public java.util.List<String> getDeliverySlotDefinitions() {
        return deliverySlotDefinitions == null || deliverySlotDefinitions.isEmpty()
                ? defaultDailySlots() : deliverySlotDefinitions;
    }
    public void setDeliverySlotDefinitions(java.util.List<String> v) { deliverySlotDefinitions = v == null ? new java.util.ArrayList<>() : v; }
    public java.util.List<String> getPickupSlotDefinitions() {
        return pickupSlotDefinitions == null || pickupSlotDefinitions.isEmpty()
                ? defaultDailySlots() : pickupSlotDefinitions;
    }
    public void setPickupSlotDefinitions(java.util.List<String> v) { pickupSlotDefinitions = v == null ? new java.util.ArrayList<>() : v; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String value) { this.upiId = value; }
    public boolean isPickupFromShop() { return pickupFromShop; }
    public void setPickupFromShop(boolean value) { this.pickupFromShop = value; }
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    public Double getShopLatitude() { return shopLatitude; }
    public void setShopLatitude(Double value) { this.shopLatitude = value; }
    public Double getShopLongitude() { return shopLongitude; }
    public void setShopLongitude(Double value) { this.shopLongitude = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
