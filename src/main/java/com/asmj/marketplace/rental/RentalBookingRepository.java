package com.asmj.marketplace.rental;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface RentalBookingRepository extends MongoRepository<RentalBooking,String> {
 List<RentalBooking> findByCustomerUserIdOrderByCreatedAtDesc(String customerUserId);
 List<RentalBooking> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);
 List<RentalBooking> findByVehicleIdOrderByCreatedAtDesc(String vehicleId);
}
