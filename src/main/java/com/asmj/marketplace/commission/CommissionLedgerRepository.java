package com.asmj.marketplace.commission;
import java.time.LocalDate; import java.util.List; import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface CommissionLedgerRepository extends MongoRepository<CommissionLedger,String> {
 Optional<CommissionLedger> findByOrderId(String orderId);
 List<CommissionLedger> findByVendorUserIdOrderByWeekStartDesc(String vendorUserId);
 List<CommissionLedger> findAllByOrderByWeekStartDesc();
 List<CommissionLedger> findByVendorUserIdAndStatusAndWeekEndBefore(String vendorUserId,String status,LocalDate date);
}
