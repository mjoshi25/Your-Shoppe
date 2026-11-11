package com.asmj.marketplace.engagement;
import java.util.List; import java.util.Optional; import org.springframework.data.mongodb.repository.MongoRepository;
public interface RecentlyViewedRepository extends MongoRepository<RecentlyViewed,String>{List<RecentlyViewed> findTop30ByCustomerUserIdOrderByViewedAtDesc(String userId); Optional<RecentlyViewed> findByCustomerUserIdAndProductId(String userId,String productId);}
