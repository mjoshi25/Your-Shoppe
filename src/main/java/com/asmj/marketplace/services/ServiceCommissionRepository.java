package com.asmj.marketplace.services;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServiceCommissionRepository extends MongoRepository<ServiceCommission,String> {
 List<ServiceCommission> findByProviderIdOrderByCreatedAtDesc(String providerId);
 List<ServiceCommission> findByStatusOrderByCreatedAtDesc(String status);
 List<ServiceCommission> findByProviderIdAndStatus(String providerId,String status);
 Optional<ServiceCommission> findByBookingId(String bookingId);
}
