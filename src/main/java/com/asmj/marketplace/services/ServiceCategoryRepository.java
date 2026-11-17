package com.asmj.marketplace.services;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServiceCategoryRepository extends MongoRepository<ServiceCategory,String> {
 List<ServiceCategory> findByParentIdIsNullAndActiveTrueOrderByNameAsc();
 List<ServiceCategory> findByParentIdAndActiveTrueOrderByNameAsc(String parentId);
 List<ServiceCategory> findByActiveTrueOrderByNameAsc();
 java.util.Optional<ServiceCategory> findByNameIgnoreCaseAndParentIdIsNullAndActiveTrue(String name);
}
