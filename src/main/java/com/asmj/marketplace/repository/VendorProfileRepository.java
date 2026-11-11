package com.asmj.marketplace.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.asmj.marketplace.model.VendorProfile;

public interface VendorProfileRepository extends MongoRepository<VendorProfile, String> {
    Optional<VendorProfile> findByUserId(String userId);
}
