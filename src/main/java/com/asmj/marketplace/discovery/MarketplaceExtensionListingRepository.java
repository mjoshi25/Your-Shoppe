package com.asmj.marketplace.discovery;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceExtensionListingRepository extends MongoRepository<MarketplaceExtensionListing,String> {
 List<MarketplaceExtensionListing> findByTypeAndStatusOrderByCreatedAtDesc(String type,String status);
 List<MarketplaceExtensionListing> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
}
