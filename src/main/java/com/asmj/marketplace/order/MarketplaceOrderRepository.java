package com.asmj.marketplace.order;
import java.util.List; import java.util.Optional; import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceOrderRepository extends MongoRepository<MarketplaceOrder,String> {
 Optional<MarketplaceOrder> findByUpiReferenceIgnoreCase(String reference);
 long countByVendorUserIdAndFulfilmentMethodAndRequestedDateAndRequestedSlotKeyAndStatusNot(String vendorUserId, String fulfilmentMethod, java.time.LocalDate requestedDate, String requestedSlotKey, OrderStatus excludedStatus); List<MarketplaceOrder> findByCustomerUserIdOrderByCreatedAtDesc(String id); List<MarketplaceOrder> findByVendorUserIdOrderByCreatedAtDesc(String id); List<MarketplaceOrder> findAllByOrderByCreatedAtDesc(); }
