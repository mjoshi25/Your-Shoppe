package com.asmj.marketplace.shopadvertising;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("shop_ad_categories")
public class ShopAdCategory {
 @Id private String id; private String name; private boolean active=true; private Instant createdAt=Instant.now();
 public String getId(){return id;} public void setId(String v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Instant getCreatedAt(){return createdAt;}
}
