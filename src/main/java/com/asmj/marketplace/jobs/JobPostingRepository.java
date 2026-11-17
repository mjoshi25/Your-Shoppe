package com.asmj.marketplace.jobs;
import java.util.*; import org.springframework.data.mongodb.repository.MongoRepository;
public interface JobPostingRepository extends MongoRepository<JobPosting,String>{ List<JobPosting> findByStatusOrderByCreatedAtDesc(String status); List<JobPosting> findByEmployerUserIdOrderByCreatedAtDesc(String userId); List<JobPosting> findByEmployerUserIdAndStatusOrderByCreatedAtDesc(String userId,String status); List<JobPosting> findAllByOrderByCreatedAtDesc(); List<JobPosting> findByStatusAndCityContainingIgnoreCaseOrderByCreatedAtDesc(String status,String city); }
