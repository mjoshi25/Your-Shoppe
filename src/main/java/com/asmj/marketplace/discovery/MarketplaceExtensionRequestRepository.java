package com.asmj.marketplace.discovery;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceExtensionRequestRepository extends MongoRepository<MarketplaceExtensionRequest,String> {
 List<MarketplaceExtensionRequest> findByRequesterUserIdOrderByCreatedAtDesc(String requesterUserId);
 List<MarketplaceExtensionRequest> findByListingIdOrderByCreatedAtDesc(String listingId);
}
