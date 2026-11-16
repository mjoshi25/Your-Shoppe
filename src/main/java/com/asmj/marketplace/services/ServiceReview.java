package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_reviews")
public class ServiceReview {
    @Id
    private String id;
    private String bookingId;
    private String listingId;
    private String providerId;
    private String customerEmail;
    private Integer rating;
    private String comment;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String value) { this.bookingId = value; }
    public String getListingId() { return listingId; }
    public void setListingId(String value) { this.listingId = value; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String value) { this.providerId = value; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String value) { this.customerEmail = value; }
    public Integer getRating() { return rating; }
    public void setRating(Integer value) { this.rating = value; }
    public String getComment() { return comment; }
    public void setComment(String value) { this.comment = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
}
