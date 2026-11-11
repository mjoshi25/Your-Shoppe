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
    private boolean homeDelivery = true;
    private boolean pickupFromShop = true;
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
    private String upiId;
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
    public String getUpiId() { return upiId; }
    public void setUpiId(String value) { this.upiId = value; }
    public boolean isPickupFromShop() { return pickupFromShop; }
    public void setPickupFromShop(boolean value) { this.pickupFromShop = value; }
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
