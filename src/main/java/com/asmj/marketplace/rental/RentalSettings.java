package com.asmj.marketplace.rental;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="rental_settings")
public class RentalSettings {
 @Id private String id="default";
 private BigDecimal commissionRatePercent=new BigDecimal("5.00");
 public String getId(){return id;} public void setId(String v){id=v;}
 public BigDecimal getCommissionRatePercent(){return commissionRatePercent;} public void setCommissionRatePercent(BigDecimal v){commissionRatePercent=v;}
}
