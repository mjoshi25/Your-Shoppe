package com.asmj.marketplace.discovery;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="community_listings")
public class CommunityListing {
 @Id private String id;
 private String type; // BUSINESS or CLASSIFIED
 private String ownerUserId;
 private String title;
 private String category;
 private String description;
 private String contactName;
 private String contactEmail;
 private String contactMobile;
 private String website;
 private String imageUrl;
 private String address;
 private String city;
 private String state;
 private String pinCode;
 private String condition;
 private java.math.BigDecimal price;
 private String status="PENDING";
 private Instant createdAt=Instant.now();
 private Instant updatedAt=Instant.now();
 public String getId(){return id;} public void setId(String v){id=v;}
 public String getType(){return type;} public void setType(String v){type=v;}
 public String getOwnerUserId(){return ownerUserId;} public void setOwnerUserId(String v){ownerUserId=v;}
 public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getContactName(){return contactName;} public void setContactName(String v){contactName=v;}
 public String getContactEmail(){return contactEmail;} public void setContactEmail(String v){contactEmail=v;}
 public String getContactMobile(){return contactMobile;} public void setContactMobile(String v){contactMobile=v;}
 public String getWebsite(){return website;} public void setWebsite(String v){website=v;}
 public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
 public String getAddress(){return address;} public void setAddress(String v){address=v;}
 public String getCity(){return city;} public void setCity(String v){city=v;}
 public String getState(){return state;} public void setState(String v){state=v;}
 public String getPinCode(){return pinCode;} public void setPinCode(String v){pinCode=v;}
 public String getCondition(){return condition;} public void setCondition(String v){condition=v;}
 public java.math.BigDecimal getPrice(){return price;} public void setPrice(java.math.BigDecimal v){price=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
 public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
