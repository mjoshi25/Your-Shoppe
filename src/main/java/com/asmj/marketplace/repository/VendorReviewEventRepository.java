package com.asmj.marketplace.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.asmj.marketplace.model.VendorReviewEvent;

public interface VendorReviewEventRepository extends MongoRepository<VendorReviewEvent, String> {
    List<VendorReviewEvent> findByVendorUserIdOrderByCreatedAtDesc(String vendorUserId);
}
