package com.asmj.marketplace.discovery;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketplaceModuleCategoryRepository extends MongoRepository<MarketplaceModuleCategory,String> {
 List<MarketplaceModuleCategory> findByTypeAndActiveTrueOrderByNameAsc(String type);
 List<MarketplaceModuleCategory> findByTypeOrderByNameAsc(String type);
 boolean existsByTypeAndNameIgnoreCase(String type,String name);
}
