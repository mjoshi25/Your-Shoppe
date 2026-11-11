package com.asmj.marketplace.chat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ProductConversationRepository extends MongoRepository<ProductConversation,String> {
    List<ProductConversation> findByVendorUserIdOrderByUpdatedAtDesc(String vendorUserId);
    List<ProductConversation> findByBuyerUserIdOrderByUpdatedAtDesc(String buyerUserId);
    List<ProductConversation> findAllByOrderByUpdatedAtDesc();
    List<ProductConversation> findByGuestSessionKeyOrderByUpdatedAtDesc(String guestSessionKey);
}
