package com.asmj.marketplace.discovery;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceFinancialEntryRepository extends MongoRepository<MarketplaceFinancialEntry,String> {
 List<MarketplaceFinancialEntry> findByReferenceIdOrderByCreatedAtAsc(String referenceId);
 List<MarketplaceFinancialEntry> findByPayerIdOrderByCreatedAtDesc(String payerId);
 List<MarketplaceFinancialEntry> findByPayeeIdOrderByCreatedAtDesc(String payeeId);
 List<MarketplaceFinancialEntry> findByPayerIdAndPaymentStatusOrderByCreatedAtDesc(String payerId,String paymentStatus);
 java.util.Optional<MarketplaceFinancialEntry> findByReferenceIdAndComponent(String referenceId,String component);


}
