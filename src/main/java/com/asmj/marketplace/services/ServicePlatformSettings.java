package com.asmj.marketplace.services;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="service_platform_settings")
public class ServicePlatformSettings {
 @Id private String id="default";
 private BigDecimal commissionRate=new BigDecimal("5.00");
 public String getId(){return id;} public void setId(String v){id=v;}
 public BigDecimal getCommissionRate(){return commissionRate;} public void setCommissionRate(BigDecimal v){commissionRate=v;}
}
