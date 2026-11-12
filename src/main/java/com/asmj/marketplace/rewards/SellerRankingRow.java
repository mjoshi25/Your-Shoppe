package com.asmj.marketplace.rewards;
import java.math.BigDecimal;
public class SellerRankingRow {
 private final String vendorUserId,vendorName;private final BigDecimal salesValue,averageRating,cancellationRate;private final int completedOrders,rank;private final boolean eligible;
 public SellerRankingRow(String vendorUserId,String vendorName,BigDecimal salesValue,int completedOrders,BigDecimal averageRating,BigDecimal cancellationRate,boolean eligible,int rank){this.vendorUserId=vendorUserId;this.vendorName=vendorName;this.salesValue=salesValue;this.completedOrders=completedOrders;this.averageRating=averageRating;this.cancellationRate=cancellationRate;this.eligible=eligible;this.rank=rank;}
 public String getVendorUserId(){return vendorUserId;}public String getVendorName(){return vendorName;}public BigDecimal getSalesValue(){return salesValue;}public int getCompletedOrders(){return completedOrders;}public BigDecimal getAverageRating(){return averageRating;}public BigDecimal getCancellationRate(){return cancellationRate;}public boolean isEligible(){return eligible;}public int getRank(){return rank;}
 public String vendorUserId(){return vendorUserId;}public String vendorName(){return vendorName;}public BigDecimal salesValue(){return salesValue;}public int completedOrders(){return completedOrders;}public BigDecimal averageRating(){return averageRating;}public BigDecimal cancellationRate(){return cancellationRate;}public boolean eligible(){return eligible;}public int rank(){return rank;}
}
