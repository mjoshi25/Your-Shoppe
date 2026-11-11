package com.asmj.marketplace.product;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface StockMovementRepository extends MongoRepository<StockMovement,String> { List<StockMovement> findTop100ByVendorUserIdOrderByCreatedAtDesc(String vendorUserId); }
