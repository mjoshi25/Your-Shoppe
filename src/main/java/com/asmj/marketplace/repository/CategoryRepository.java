package com.asmj.marketplace.repository;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.asmj.marketplace.model.Category;
public interface CategoryRepository extends MongoRepository<Category,String> {
    List<Category> findByActiveTrueOrderByNameAsc();
}
