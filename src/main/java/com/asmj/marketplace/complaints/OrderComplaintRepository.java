package com.asmj.marketplace.complaints;
import java.util.List; import org.springframework.data.mongodb.repository.MongoRepository;
public interface OrderComplaintRepository extends MongoRepository<OrderComplaint,String> {
 List<OrderComplaint> findByCustomerUserIdOrderByCreatedAtDesc(String customerUserId);
 List<OrderComplaint> findByVendorUserIdOrderByCreatedAtDesc(String vendorUserId);
 List<OrderComplaint> findAllByOrderByCreatedAtDesc();
 boolean existsByOrderIdAndCustomerUserIdAndStatusIn(String orderId,String customerUserId,List<OrderComplaint.Status> statuses);
}
