package com.asmj.marketplace.discovery;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface CommunityListingRepository extends MongoRepository<CommunityListing,String> {
 List<CommunityListing> findByTypeAndStatusOrderByCreatedAtDesc(String type,String status);
 List<CommunityListing> findByTypeOrderByCreatedAtDesc(String type);
 List<CommunityListing> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
}
