package com.asmj.marketplace.notification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface NotificationRepository extends MongoRepository<MarketplaceNotification,String> {
 List<MarketplaceNotification> findTop100ByRecipientUserIdOrderByCreatedAtDesc(String userId);
 long countByRecipientUserIdAndReadFalse(String userId);
}
