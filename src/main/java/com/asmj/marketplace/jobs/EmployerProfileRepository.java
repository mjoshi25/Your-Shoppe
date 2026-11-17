package com.asmj.marketplace.jobs;
import java.util.*; import org.springframework.data.mongodb.repository.MongoRepository;
public interface EmployerProfileRepository extends MongoRepository<EmployerProfile,String>{ Optional<EmployerProfile> findByUserId(String userId); List<EmployerProfile> findByStatusOrderByCreatedAtDesc(String status); List<EmployerProfile> findAllByOrderByCreatedAtDesc(); }
