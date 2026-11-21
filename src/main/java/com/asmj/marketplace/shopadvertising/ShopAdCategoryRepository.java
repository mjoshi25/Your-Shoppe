package com.asmj.marketplace.shopadvertising;
import java.util.*; import org.springframework.data.mongodb.repository.MongoRepository;
public interface ShopAdCategoryRepository extends MongoRepository<ShopAdCategory,String>{List<ShopAdCategory> findByActiveTrueOrderByNameAsc(); Optional<ShopAdCategory> findByNameIgnoreCase(String name);}
