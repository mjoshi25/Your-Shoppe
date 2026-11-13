package com.asmj.marketplace.product;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id private String id;
    @Indexed private String vendorUserId;
    private String vendorName;
    private String name;
    private java.time.Instant stockUpdatedAt = java.time.Instant.now();
    private String description;
    private String categoryId;
    private String categoryName;
    private String subcategoryName;
    private boolean homeDelivery;
    private boolean pickupFromShop;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String imagePublicId;
    @Indexed private ProductStatus status = ProductStatus.PENDING;
    private Instant createdAt = Instant.now();
    /** Identifies products created by the controlled application test-data seeder. */
    private boolean seededTestProduct = false;
    private String seedBatchId;
    private String seedKey;
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getVendorUserId(){return vendorUserId;} public void setVendorUserId(String v){vendorUserId=v;}
    public String getVendorName(){return vendorName;} public void setVendorName(String v){vendorName=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public java.time.Instant getStockUpdatedAt(){return stockUpdatedAt;} public void setStockUpdatedAt(java.time.Instant v){stockUpdatedAt=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getCategoryId(){return categoryId;} public void setCategoryId(String v){categoryId=v;}
    public String getCategoryName(){return categoryName;} public void setCategoryName(String v){categoryName=v;}
    public String getSubcategoryName(){return subcategoryName;} public void setSubcategoryName(String v){subcategoryName=v;}
    public boolean isHomeDelivery(){return homeDelivery;} public void setHomeDelivery(boolean v){homeDelivery=v;}
    public boolean isPickupFromShop(){return pickupFromShop;} public void setPickupFromShop(boolean v){pickupFromShop=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public Integer getStockQuantity(){return stockQuantity;} public void setStockQuantity(Integer v){stockQuantity=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
    public String getImagePublicId(){return imagePublicId;} public void setImagePublicId(String v){imagePublicId=v;}
    public ProductStatus getStatus(){return status;} public void setStatus(ProductStatus v){status=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public boolean isSeededTestProduct(){return seededTestProduct;} public void setSeededTestProduct(boolean v){seededTestProduct=v;}
    public String getSeedBatchId(){return seedBatchId;} public void setSeedBatchId(String v){seedBatchId=v;}
    public String getSeedKey(){return seedKey;} public void setSeedKey(String v){seedKey=v;}
}
