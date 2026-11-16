package com.asmj.marketplace.services;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServiceReviewRepository extends MongoRepository<ServiceReview,String> { List<ServiceReview> findByListingIdOrderByCreatedAtDesc(String listingId); boolean existsByBookingId(String bookingId); }
