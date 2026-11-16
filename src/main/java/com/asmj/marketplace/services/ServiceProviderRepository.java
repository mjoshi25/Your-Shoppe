package com.asmj.marketplace.services;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServiceProviderRepository extends MongoRepository<ServiceProvider,String> {
 Optional<ServiceProvider> findByEmailIgnoreCase(String email);
 List<ServiceProvider> findByStatusOrderByCreatedAtDesc(String status);
 List<ServiceProvider> findByStatusAndPinCodesContainingOrderByDisplayNameAsc(String status,String pin);
}
