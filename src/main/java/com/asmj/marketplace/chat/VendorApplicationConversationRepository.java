package com.asmj.marketplace.chat;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VendorApplicationConversationRepository extends MongoRepository<VendorApplicationConversation, String> {
    Optional<VendorApplicationConversation> findByVendorUserId(String vendorUserId);
}
