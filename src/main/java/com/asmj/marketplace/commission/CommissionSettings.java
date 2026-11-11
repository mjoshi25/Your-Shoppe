package com.asmj.marketplace.commission;
import java.math.BigDecimal; import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="marketplace_settings") public class CommissionSettings { @Id private String id="commission"; private BigDecimal ratePercent=new BigDecimal("5.00"); public String getId(){return id;} public void setId(String v){id=v;} public BigDecimal getRatePercent(){return ratePercent;} public void setRatePercent(BigDecimal v){ratePercent=v;} }
