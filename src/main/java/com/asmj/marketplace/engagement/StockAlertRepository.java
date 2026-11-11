package com.asmj.marketplace.engagement;
import java.util.List; import java.util.Optional; import org.springframework.data.mongodb.repository.MongoRepository;
public interface StockAlertRepository extends MongoRepository<StockAlertSubscription,String>{Optional<StockAlertSubscription> findByCustomerUserIdAndProductId(String userId,String productId); List<StockAlertSubscription> findByProductIdAndNotifiedFalse(String productId); List<StockAlertSubscription> findByCustomerUserIdOrderByCreatedAtDesc(String userId);}
