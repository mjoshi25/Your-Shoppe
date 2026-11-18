package com.asmj.marketplace.emergency;
import java.util.List;import org.springframework.data.mongodb.repository.MongoRepository;
public interface EmergencyAssistanceRequestRepository extends MongoRepository<EmergencyAssistanceRequest,String>{List<EmergencyAssistanceRequest> findByCustomerUserIdOrderByCreatedAtDesc(String customerUserId);}
