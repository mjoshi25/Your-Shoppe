package com.asmj.marketplace.commission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="marketplace_settings")
public class CommissionSettings {
    @Id private String id="commission";
    private BigDecimal fixedAmount=BigDecimal.ZERO;
    private BigDecimal ratePercent; // legacy single-rate field for existing MongoDB documents
    private List<CommissionSlab> slabs=new ArrayList<>();

    public String getId(){return id;} public void setId(String v){id=v;}
    public BigDecimal getFixedAmount(){return fixedAmount==null?BigDecimal.ZERO:fixedAmount;}
    public void setFixedAmount(BigDecimal v){fixedAmount=v;}
    public List<CommissionSlab> getSlabs(){if(slabs==null)slabs=new ArrayList<>();return slabs;}
    public void setSlabs(List<CommissionSlab> v){slabs=v==null?new ArrayList<>():v;}
    /** Compatibility accessor for older screens and integrations. */
    public BigDecimal getRatePercent(){return getSlabs().isEmpty()?(ratePercent==null?BigDecimal.ZERO:ratePercent):getSlabs().get(0).getRatePercent();}
    public void setRatePercent(BigDecimal v){ratePercent=v;getSlabs().clear();getSlabs().add(new CommissionSlab(BigDecimal.ZERO,null,v));}

    public static class CommissionSlab {
        private BigDecimal minAmount=BigDecimal.ZERO;
        private BigDecimal maxAmount;
        private BigDecimal ratePercent=BigDecimal.ZERO;
        public CommissionSlab(){}
        public CommissionSlab(BigDecimal min,BigDecimal max,BigDecimal rate){minAmount=min;maxAmount=max;ratePercent=rate;}
        public BigDecimal getMinAmount(){return minAmount;} public void setMinAmount(BigDecimal v){minAmount=v;}
        public BigDecimal getMaxAmount(){return maxAmount;} public void setMaxAmount(BigDecimal v){maxAmount=v;}
        public BigDecimal getRatePercent(){return ratePercent;} public void setRatePercent(BigDecimal v){ratePercent=v;}
    }
}
