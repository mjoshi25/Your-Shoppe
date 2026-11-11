package com.asmj.marketplace.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vendor_review_events")
public class VendorReviewEvent {
    @Id
    private String id;
    @Indexed
    private String vendorUserId;
    private VendorStatus fromStatus;
    private VendorStatus toStatus;
    private String reviewedBy;
    private String reason;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVendorUserId() { return vendorUserId; }
    public void setVendorUserId(String vendorUserId) { this.vendorUserId = vendorUserId; }
    public VendorStatus getFromStatus() { return fromStatus; }
    public void setFromStatus(VendorStatus fromStatus) { this.fromStatus = fromStatus; }
    public VendorStatus getToStatus() { return toStatus; }
    public void setToStatus(VendorStatus toStatus) { this.toStatus = toStatus; }
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
