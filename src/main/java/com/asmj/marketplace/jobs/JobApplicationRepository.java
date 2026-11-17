package com.asmj.marketplace.jobs;
import java.util.*; import org.springframework.data.mongodb.repository.MongoRepository;
public interface JobApplicationRepository extends MongoRepository<JobApplication,String>{ boolean existsByJobIdAndCandidateUserId(String jobId,String candidateUserId); List<JobApplication> findByJobIdOrderByCreatedAtDesc(String jobId); List<JobApplication> findByCandidateUserIdOrderByCreatedAtDesc(String userId); }
