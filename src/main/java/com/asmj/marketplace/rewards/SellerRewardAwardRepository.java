package com.asmj.marketplace.rewards;
import java.time.LocalDate;import java.util.List;import org.springframework.data.mongodb.repository.MongoRepository;
public interface SellerRewardAwardRepository extends MongoRepository<SellerRewardAward,String>{List<SellerRewardAward> findByVendorUserIdOrderByPeriodEndDescRankAsc(String vendorUserId);List<SellerRewardAward> findByPeriodStartAndPeriodEndOrderByRankAsc(LocalDate start,LocalDate end);List<SellerRewardAward> findAllByOrderByPeriodEndDescRankAsc();void deleteByPeriodStartAndPeriodEnd(LocalDate start,LocalDate end);}
