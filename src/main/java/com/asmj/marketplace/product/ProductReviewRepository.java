package com.asmj.marketplace.product;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ProductReviewRepository extends MongoRepository<ProductReview,String> {
 List<ProductReview> findByProductIdAndStatusOrderByCreatedAtDesc(String productId, ProductReview.ReviewStatus status);
 List<ProductReview> findByVendorUserIdOrderByCreatedAtDesc(String vendorUserId);
 List<ProductReview> findByStatusOrderByCreatedAtDesc(ProductReview.ReviewStatus status);
 List<ProductReview> findByCustomerUserId(String customerUserId);
 boolean existsByProductIdAndCustomerUserIdAndOrderId(String productId,String customerUserId,String orderId);
}
