package com.asmj.marketplace.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rental_vehicles")
public class RentalVehicle {
    @Id private String id;
    @Indexed private String ownerUserId;
    private String ownerName;
    private String vehicleCategory;
    private String vehicleSubcategory;
    private String makeModel;
    private String registrationNumber;
    private int seatingCapacity;
    private String description;
    private String imageUrl;
    private String driverName;
    private String driverMobile;
    private String driverLicenceNumber;
    private LocalDate driverLicenceValidUntil;
    private LocalDate insuranceValidUntil;
    private BigDecimal baseFare = BigDecimal.ZERO;
    private BigDecimal perKmRate = BigDecimal.ZERO;
    private String status = "PENDING";
    private String adminNote;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    /** Marks records created by the opt-in rental sample-data seeder. */
    private boolean seededTestData = false;
    private String seedBatchId;
    private String seedKey;

    public String getId(){return id;} public void setId(String v){id=v;}
    public String getOwnerUserId(){return ownerUserId;} public void setOwnerUserId(String v){ownerUserId=v;}
    public String getOwnerName(){return ownerName;} public void setOwnerName(String v){ownerName=v;}
    public String getVehicleCategory(){return vehicleCategory;} public void setVehicleCategory(String v){vehicleCategory=v;}
    public String getVehicleSubcategory(){return vehicleSubcategory;} public void setVehicleSubcategory(String v){vehicleSubcategory=v;}
    public String getMakeModel(){return makeModel;} public void setMakeModel(String v){makeModel=v;}
    public String getRegistrationNumber(){return registrationNumber;} public void setRegistrationNumber(String v){registrationNumber=v;}
    public int getSeatingCapacity(){return seatingCapacity;} public void setSeatingCapacity(int v){seatingCapacity=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
    public String getDriverName(){return driverName;} public void setDriverName(String v){driverName=v;}
    public String getDriverMobile(){return driverMobile;} public void setDriverMobile(String v){driverMobile=v;}
    public String getDriverLicenceNumber(){return driverLicenceNumber;} public void setDriverLicenceNumber(String v){driverLicenceNumber=v;}
    public LocalDate getDriverLicenceValidUntil(){return driverLicenceValidUntil;} public void setDriverLicenceValidUntil(LocalDate v){driverLicenceValidUntil=v;}
    public LocalDate getInsuranceValidUntil(){return insuranceValidUntil;} public void setInsuranceValidUntil(LocalDate v){insuranceValidUntil=v;}
    public BigDecimal getBaseFare(){return baseFare;} public void setBaseFare(BigDecimal v){baseFare=v;}
    public BigDecimal getPerKmRate(){return perKmRate;} public void setPerKmRate(BigDecimal v){perKmRate=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getAdminNote(){return adminNote;} public void setAdminNote(String v){adminNote=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public boolean isSeededTestData(){return seededTestData;} public void setSeededTestData(boolean v){seededTestData=v;}
    public String getSeedBatchId(){return seedBatchId;} public void setSeedBatchId(String v){seedBatchId=v;}
    public String getSeedKey(){return seedKey;} public void setSeedKey(String v){seedKey=v;}
    public boolean isEligible(){return "APPROVED".equals(status) && driverLicenceValidUntil!=null && !driverLicenceValidUntil.isBefore(LocalDate.now()) && insuranceValidUntil!=null && !insuranceValidUntil.isBefore(LocalDate.now());}
}
