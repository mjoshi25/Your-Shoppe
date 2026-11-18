package com.asmj.marketplace.emergency;
import java.util.List;import java.util.Optional;import org.springframework.data.mongodb.repository.MongoRepository;
public interface EmergencyProviderRepository extends MongoRepository<EmergencyProvider,String>{List<EmergencyProvider> findByStatusAndAvailabilityOrderByProviderNameAsc(String status,String availability);List<EmergencyProvider> findByStatusOrderByProviderNameAsc(String status);List<EmergencyProvider> findByOwnerUserIdOrderByUpdatedAtDesc(String ownerUserId);Optional<EmergencyProvider> findByIdAndOwnerUserId(String id,String ownerUserId);}
