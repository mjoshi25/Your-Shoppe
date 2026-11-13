package com.asmj.marketplace.order;
import java.math.BigDecimal;
public class OrderItem {
 private String productId, productName, imageUrl; private int quantity; private BigDecimal unitPrice, lineTotal, couponDiscount=BigDecimal.ZERO;
 public OrderItem(){} public OrderItem(String id,String name,String image,int qty,BigDecimal price){productId=id;productName=name;imageUrl=image;quantity=qty;unitPrice=price;lineTotal=price.multiply(BigDecimal.valueOf(qty));}
 public String getProductId(){return productId;} public void setProductId(String v){productId=v;} public String getProductName(){return productName;} public void setProductName(String v){productName=v;} public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;} public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;} public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;} public BigDecimal getLineTotal(){return lineTotal;} public void setLineTotal(BigDecimal v){lineTotal=v;}
 public BigDecimal getCouponDiscount(){return couponDiscount==null?BigDecimal.ZERO:couponDiscount;} public void setCouponDiscount(BigDecimal v){couponDiscount=v==null?BigDecimal.ZERO:v;}
}
