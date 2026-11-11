package com.asmj.marketplace.engagement;
import java.util.List; import java.util.Optional; import org.springframework.data.mongodb.repository.MongoRepository;
public interface CouponRepository extends MongoRepository<DiscountCoupon,String>{Optional<DiscountCoupon> findByCodeIgnoreCase(String code); List<DiscountCoupon> findAllByOrderByCreatedAtDesc();}
