package com.asmj.marketplace.chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="product_conversations")
public class ProductConversation {
    @Id private String id;
    @Indexed private String productId;
    private String productName;
    private String productImageUrl;
    @Indexed private String vendorUserId;
    private String vendorName;
    @Indexed private String buyerUserId;
    @Indexed private String guestSessionKey;
    private String buyerName;
    private String buyerMobile;
    private boolean guest;
    private List<ChatMessage> messages = new ArrayList<>();
    private Instant createdAt=Instant.now();
    private Instant updatedAt=Instant.now();
    private Instant buyerLastReadAt;
    private Instant vendorLastReadAt;
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getProductId(){return productId;} public void setProductId(String v){productId=v;}
    public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getProductImageUrl(){return productImageUrl;} public void setProductImageUrl(String v){productImageUrl=v;}
    public String getVendorUserId(){return vendorUserId;} public void setVendorUserId(String v){vendorUserId=v;}
    public String getVendorName(){return vendorName;} public void setVendorName(String v){vendorName=v;}
    public String getBuyerUserId(){return buyerUserId;} public void setBuyerUserId(String v){buyerUserId=v;}
    public String getGuestSessionKey(){return guestSessionKey;} public void setGuestSessionKey(String v){guestSessionKey=v;}
    public String getBuyerName(){return buyerName;} public void setBuyerName(String v){buyerName=v;}
    public String getBuyerMobile(){return buyerMobile;} public void setBuyerMobile(String v){buyerMobile=v;}
    public boolean isGuest(){return guest;} public void setGuest(boolean v){guest=v;}
    public List<ChatMessage> getMessages(){return messages;} public void setMessages(List<ChatMessage> v){messages=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public Instant getBuyerLastReadAt(){return buyerLastReadAt;} public void setBuyerLastReadAt(Instant v){buyerLastReadAt=v;}
    public Instant getVendorLastReadAt(){return vendorLastReadAt;} public void setVendorLastReadAt(Instant v){vendorLastReadAt=v;}
}
