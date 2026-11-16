package com.asmj.marketplace.services;
import java.time.Instant;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
public interface ServiceBookingRepository extends MongoRepository<ServiceBooking,String> {
 List<ServiceBooking> findByCustomerEmailOrderByCreatedAtDesc(String email);
 List<ServiceBooking> findByProviderIdOrderByCreatedAtDesc(String providerId);
 @Query("{ 'providerId': ?0, 'status': { '$in': ['PENDING','ACCEPTED','IN_PROGRESS','AWAITING_CUSTOMER_CONFIRMATION'] }, 'appointmentStart': { '$lt': ?2 }, 'appointmentEnd': { '$gt': ?1 } }")
 List<ServiceBooking> findConflicts(String providerId, Instant start, Instant end);
}
