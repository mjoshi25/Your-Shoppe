package com.asmj.marketplace.chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Persistent one-to-one discussion between marketplace administration and a vendor application. */
@Document(collection = "vendor_application_conversations")
public class VendorApplicationConversation {
    @Id private String id;
    @Indexed(unique = true) private String vendorUserId;
    private String vendorName;
    private String vendorEmail;
    private List<ChatMessage> messages = new ArrayList<>();
    private Instant updatedAt = Instant.now();
    private Instant adminLastReadAt;
    private Instant vendorLastReadAt;

    public String getId(){return id;} public void setId(String v){id=v;}
    public String getVendorUserId(){return vendorUserId;} public void setVendorUserId(String v){vendorUserId=v;}
    public String getVendorName(){return vendorName;} public void setVendorName(String v){vendorName=v;}
    public String getVendorEmail(){return vendorEmail;} public void setVendorEmail(String v){vendorEmail=v;}
    public List<ChatMessage> getMessages(){if(messages==null)messages=new ArrayList<>();return messages;} public void setMessages(List<ChatMessage> v){messages=v==null?new ArrayList<>():v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public Instant getAdminLastReadAt(){return adminLastReadAt;} public void setAdminLastReadAt(Instant v){adminLastReadAt=v;}
    public Instant getVendorLastReadAt(){return vendorLastReadAt;} public void setVendorLastReadAt(Instant v){vendorLastReadAt=v;}
}
