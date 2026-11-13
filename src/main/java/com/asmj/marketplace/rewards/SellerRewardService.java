package com.asmj.marketplace.rewards;

import com.asmj.marketplace.commission.CommissionService;
import com.asmj.marketplace.model.*;
import com.asmj.marketplace.order.*;
import com.asmj.marketplace.product.*;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.math.*;import java.time.*;import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SellerRewardService {
 private final MarketplaceOrderRepository orders;private final UserAccountRepository users;private final ProductReviewRepository reviews;private final CommissionService commissions;
 public SellerRewardService(MarketplaceOrderRepository orders,UserAccountRepository users,ProductReviewRepository reviews,CommissionService commissions){this.orders=orders;this.users=users;this.reviews=reviews;this.commissions=commissions;}
 public List<SellerRankingRow> ranking(SellerRewardProgram program){
  if(program==null||program.getPeriodStart()==null||program.getPeriodEnd()==null)return List.of();
  Instant from=program.getPeriodStart().atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();Instant to=program.getPeriodEnd().plusDays(1).atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
  List<MarketplaceOrder> all=orders.findAllByOrderByCreatedAtDesc();List<SellerRankingRow> rows=new ArrayList<>();
  for(UserAccount vendor:users.findByRoleOrderByCreatedAtDesc(Role.ROLE_VENDOR)){
   if(vendor.getVendorStatus()!=VendorStatus.APPROVED)continue;
   List<MarketplaceOrder> vendorOrders=all.stream().filter(o->vendor.getId().equals(o.getVendorUserId())).toList();
   List<MarketplaceOrder> periodCreated=vendorOrders.stream().filter(o->o.getCreatedAt()!=null&&!o.getCreatedAt().isBefore(from)&&o.getCreatedAt().isBefore(to)).toList();
   List<MarketplaceOrder> completed=vendorOrders.stream().filter(o->o.getStatus()==OrderStatus.COMPLETED&&o.getPaymentStatus()==PaymentStatus.VERIFIED&&o.getNetReceived().signum()>0&&o.getSubtotal()!=null&&o.getSubtotal().subtract(o.getDiscountAmount()==null?BigDecimal.ZERO:o.getDiscountAmount()).subtract(o.getMerchandiseRefunds()).max(BigDecimal.ZERO).signum()>0&&o.getCompletedAt()!=null&&!o.getCompletedAt().isBefore(from)&&o.getCompletedAt().isBefore(to)).toList();
   BigDecimal sales=completed.stream().map(o->(o.getSubtotal()==null?BigDecimal.ZERO:o.getSubtotal()).subtract(o.getDiscountAmount()==null?BigDecimal.ZERO:o.getDiscountAmount()).subtract(o.getMerchandiseRefunds()).max(BigDecimal.ZERO)).reduce(BigDecimal.ZERO,BigDecimal::add);
   long cancelled=periodCreated.stream().filter(o->o.getStatus()==OrderStatus.CANCELLED).count();BigDecimal cancellation=periodCreated.isEmpty()?BigDecimal.ZERO:BigDecimal.valueOf(cancelled*100.0/periodCreated.size()).setScale(2,RoundingMode.HALF_UP);
   List<ProductReview> approved=reviews.findByVendorUserIdOrderByCreatedAtDesc(vendor.getId()).stream().filter(r->r.getStatus()==ProductReview.ReviewStatus.APPROVED&&r.getCreatedAt()!=null&&!r.getCreatedAt().isBefore(from)&&r.getCreatedAt().isBefore(to)).toList();BigDecimal rating=approved.isEmpty()?BigDecimal.ZERO:BigDecimal.valueOf(approved.stream().mapToInt(ProductReview::getRating).average().orElse(0)).setScale(2,RoundingMode.HALF_UP);
   boolean eligible=completed.size()>=Math.max(1,program.getMinimumCompletedOrders())&&!commissions.hasOverdueCommission(vendor.getId());
   rows.add(new SellerRankingRow(vendor.getId(),vendor.getFullName(),sales,completed.size(),rating,cancellation,eligible,0));
  }
  rows.sort(Comparator.comparing(SellerRankingRow::eligible).reversed().thenComparing(SellerRankingRow::salesValue,Comparator.reverseOrder()).thenComparing(SellerRankingRow::averageRating,Comparator.reverseOrder()).thenComparing(SellerRankingRow::cancellationRate).thenComparing(SellerRankingRow::completedOrders,Comparator.reverseOrder()).thenComparing(SellerRankingRow::vendorName,String.CASE_INSENSITIVE_ORDER));
  List<SellerRankingRow> ranked=new ArrayList<>();int rank=0;for(SellerRankingRow r:rows){int assigned=0;if(r.eligible()){rank++;if(rank<=5)assigned=rank;}ranked.add(new SellerRankingRow(r.vendorUserId(),r.vendorName(),r.salesValue(),r.completedOrders(),r.averageRating(),r.cancellationRate(),r.eligible(),assigned));}
  return ranked;
 }
}
