package com.asmj.marketplace.engagement;
import java.util.List; import java.util.Optional; import org.springframework.data.mongodb.repository.MongoRepository;
public interface WishlistRepository extends MongoRepository<WishlistItem,String>{List<WishlistItem> findByCustomerUserIdOrderByCreatedAtDesc(String userId); Optional<WishlistItem> findByCustomerUserIdAndProductId(String userId,String productId); void deleteByCustomerUserIdAndProductId(String userId,String productId);}
