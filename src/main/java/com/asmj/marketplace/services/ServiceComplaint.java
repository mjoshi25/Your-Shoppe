package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_complaints")
public class ServiceComplaint {
    @Id
    private String id;
    private String bookingId;
    private String raisedBy;
    private String subject;
    private String description;
    private String status = "PENDING";
    private String adminNote;
    private Instant createdAt = Instant.now();
    private Instant resolvedAt;

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String value) { this.bookingId = value; }
    public String getRaisedBy() { return raisedBy; }
    public void setRaisedBy(String value) { this.raisedBy = value; }
    public String getSubject() { return subject; }
    public void setSubject(String value) { this.subject = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String value) { this.adminNote = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant value) { this.resolvedAt = value; }
}
