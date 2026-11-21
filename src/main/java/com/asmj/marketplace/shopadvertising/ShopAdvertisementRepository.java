package com.asmj.marketplace.shopadvertising;
import java.util.*; import org.springframework.data.mongodb.repository.MongoRepository;
public interface ShopAdvertisementRepository extends MongoRepository<ShopAdvertisement,String>{List<ShopAdvertisement> findByVendorUserIdOrderByCreatedAtDesc(String vendorUserId); List<ShopAdvertisement> findAllByOrderByCreatedAtDesc(); List<ShopAdvertisement> findByPaymentStatusAndApprovalStatusAndStatus(String paymentStatus,String approvalStatus,String status); boolean existsByPaymentUtrIgnoreCaseAndIdNot(String utr,String id);}
