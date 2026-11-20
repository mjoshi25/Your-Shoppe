package com.asmj.marketplace.discovery;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event_booking_settings")
public class EventBookingSettings {
    @Id private String id = "default";
    private boolean enabled = true;
    private String chargeMode = "FIXED"; // FIXED, PERCENTAGE, BOTH
    private BigDecimal fixedCharge = BigDecimal.ZERO;
    private BigDecimal percentageCharge = BigDecimal.ZERO;
    private Instant updatedAt = Instant.now();
    private String updatedBy;
    public String getId(){return id;} public void setId(String v){id=v;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
    public String getChargeMode(){return chargeMode;} public void setChargeMode(String v){chargeMode=v;}
    public BigDecimal getFixedCharge(){return fixedCharge;} public void setFixedCharge(BigDecimal v){fixedCharge=v;}
    public BigDecimal getPercentageCharge(){return percentageCharge;} public void setPercentageCharge(BigDecimal v){percentageCharge=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;}
}
