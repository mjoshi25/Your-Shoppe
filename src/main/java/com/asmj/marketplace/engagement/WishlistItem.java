package com.asmj.marketplace.engagement;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("wishlist_items") @CompoundIndex(name="wishlist_user_product_uq",def="{'customerUserId':1,'productId':1}",unique=true)
public class WishlistItem { @Id private String id; private String customerUserId; private String productId; private Instant createdAt=Instant.now(); public String getId(){return id;} public void setId(String v){id=v;} public String getCustomerUserId(){return customerUserId;} public void setCustomerUserId(String v){customerUserId=v;} public String getProductId(){return productId;} public void setProductId(String v){productId=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} }
