package com.asmj.marketplace.services;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServicePaymentRepository extends MongoRepository<ServicePayment,String> { Optional<ServicePayment> findByBookingId(String bookingId);
 java.util.List<ServicePayment> findByProviderIdOrderByCreatedAtDesc(String providerId); }
