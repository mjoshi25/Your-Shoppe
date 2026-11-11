package com.asmj.marketplace.order;
import java.util.List; import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceOrderRepository extends MongoRepository<MarketplaceOrder,String> { List<MarketplaceOrder> findByCustomerUserIdOrderByCreatedAtDesc(String id); List<MarketplaceOrder> findByVendorUserIdOrderByCreatedAtDesc(String id); List<MarketplaceOrder> findAllByOrderByCreatedAtDesc(); }
