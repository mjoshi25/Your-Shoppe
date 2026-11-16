package com.asmj.marketplace.services;
import java.util.*;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ServiceComplaintRepository extends MongoRepository<ServiceComplaint,String> { List<ServiceComplaint> findAllByOrderByCreatedAtDesc(); }
