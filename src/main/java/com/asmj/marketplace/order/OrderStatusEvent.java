package com.asmj.marketplace.order;

import java.time.Instant;

public class OrderStatusEvent {
    private OrderStatus status;
    private String note;
    private String updatedBy;
    private String updatedByRole;
    private Instant createdAt = Instant.now();
    public OrderStatusEvent() {}
    public OrderStatusEvent(OrderStatus status, String note, String updatedBy, String updatedByRole) { this.status=status; this.note=note; this.updatedBy=updatedBy; this.updatedByRole=updatedByRole; this.createdAt=Instant.now(); }
    public OrderStatus getStatus(){return status;} public void setStatus(OrderStatus v){status=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;}
    public String getUpdatedByRole(){return updatedByRole;} public void setUpdatedByRole(String v){updatedByRole=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
