package com.asmj.marketplace.services;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServiceListingRepository extends MongoRepository<ServiceListing,String> {
 List<ServiceListing> findByStatusAndActiveTrueOrderByCreatedAtDesc(String status);
 List<ServiceListing> findByProviderIdOrderByCreatedAtDesc(String providerId);
 List<ServiceListing> findByStatusAndActiveTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(String status,String q);
 List<ServiceListing> findByStatusAndActiveTrueAndCategoryIdOrderByCreatedAtDesc(String status,String categoryId);
}
