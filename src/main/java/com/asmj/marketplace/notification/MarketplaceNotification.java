package com.asmj.marketplace.notification;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "marketplace_notifications")
public class MarketplaceNotification {
    @Id private String id;
    @Indexed private String recipientUserId;
    private String title;
    private String message;
    private String targetType;
    private String targetId;
    private boolean read;
    private Instant createdAt = Instant.now();
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getRecipientUserId(){return recipientUserId;} public void setRecipientUserId(String v){recipientUserId=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public String getTargetType(){return targetType;} public void setTargetType(String v){targetType=v;}
    public String getTargetId(){return targetId;} public void setTargetId(String v){targetId=v;}
    public boolean isRead(){return read;} public void setRead(boolean v){read=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
