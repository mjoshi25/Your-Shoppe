package com.asmj.marketplace.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "service_categories")
public class ServiceCategory {
    @Id
    private String id;
    private String name;
    private String description;
    private String parentId;
    private boolean active = true;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String value) { this.id = value; }
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }
    public String getParentId() { return parentId; }
    public void setParentId(String value) { this.parentId = value; }
    public boolean getActive() { return active; }
    public void setActive(boolean value) { this.active = value; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant value) { this.createdAt = value; }
}
