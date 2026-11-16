package com.asmj.marketplace.contact;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ContactQueryRepository extends MongoRepository<ContactQuery,String> {
    List<ContactQuery> findAllByOrderByCreatedAtDesc();
}
