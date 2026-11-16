package com.asmj.marketplace.rental;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface RentalVehicleRepository extends MongoRepository<RentalVehicle,String> {
 List<RentalVehicle> findByStatusOrderByCreatedAtDesc(String status);
 List<RentalVehicle> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
 boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
}
