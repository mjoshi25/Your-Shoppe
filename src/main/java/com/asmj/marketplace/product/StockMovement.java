package com.asmj.marketplace.product;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="stock_movements")
public class StockMovement {
 @Id private String id; @Indexed private String productId; @Indexed private String vendorUserId; private String productName; private int previousQuantity; private int newQuantity; private String reason; private String changedBy; private Instant createdAt=Instant.now();
 public String getId(){return id;} public void setId(String v){id=v;} public String getProductId(){return productId;} public void setProductId(String v){productId=v;} public String getVendorUserId(){return vendorUserId;} public void setVendorUserId(String v){vendorUserId=v;} public String getProductName(){return productName;} public void setProductName(String v){productName=v;} public int getPreviousQuantity(){return previousQuantity;} public void setPreviousQuantity(int v){previousQuantity=v;} public int getNewQuantity(){return newQuantity;} public void setNewQuantity(int v){newQuantity=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public String getChangedBy(){return changedBy;} public void setChangedBy(String v){changedBy=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
