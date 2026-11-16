package com.asmj.marketplace.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rental_bookings")
public class RentalBooking {
    @Id private String id;
    @Indexed private String customerUserId;
    @Indexed private String ownerUserId;
    @Indexed private String vehicleId;
    private String bookingNumber;
    private String customerName;
    private String customerMobile;
    private String vehicleName;
    private String vehicleCategory;
    private String tripType;
    private String pickupAddress;
    private String pickupPinCode;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String dropAddress;
    private String dropPinCode;
    private Double dropLatitude;
    private Double dropLongitude;
    private BigDecimal distanceKm;
    private LocalDate pickupDate;
    private LocalTime pickupTime;
    private LocalDate returnDate;
    private LocalTime returnTime;
    private int passengers;
    private String customerNote;
    private BigDecimal estimatedFare;
    private BigDecimal ownerQuotedFare;
    private String status = "REQUESTED";
    private String ownerResponseNote;
    private String paymentMethod;
    private String paymentStatus = "PENDING";
    private String paymentUtr;
    private String paymentProofUrl;
    private String paymentProofName;
    private String paymentReviewNote;
    private BigDecimal amountPaid = BigDecimal.ZERO;
    private Integer startOdometerKm;
    private Integer endOdometerKm;
    private BigDecimal platformCommissionRate = new BigDecimal("5.00");
    private BigDecimal platformCommissionAmount = BigDecimal.ZERO;
    private String commissionStatus = "NOT_DUE";
    private String commissionPaymentUtr;
    private String commissionPaymentProofUrl;
    private Instant completedAt;
    private Instant cancelledAt;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public String getId(){return id;} public void setId(String v){id=v;}
    public String getCustomerUserId(){return customerUserId;} public void setCustomerUserId(String v){customerUserId=v;}
    public String getOwnerUserId(){return ownerUserId;} public void setOwnerUserId(String v){ownerUserId=v;}
    public String getVehicleId(){return vehicleId;} public void setVehicleId(String v){vehicleId=v;}
    public String getBookingNumber(){return bookingNumber;} public void setBookingNumber(String v){bookingNumber=v;}
    public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;}
    public String getCustomerMobile(){return customerMobile;} public void setCustomerMobile(String v){customerMobile=v;}
    public String getVehicleName(){return vehicleName;} public void setVehicleName(String v){vehicleName=v;}
    public String getVehicleCategory(){return vehicleCategory;} public void setVehicleCategory(String v){vehicleCategory=v;}
    public String getTripType(){return tripType;} public void setTripType(String v){tripType=v;}
    public String getPickupAddress(){return pickupAddress;} public void setPickupAddress(String v){pickupAddress=v;}
    public String getPickupPinCode(){return pickupPinCode;} public void setPickupPinCode(String v){pickupPinCode=v;}
    public Double getPickupLatitude(){return pickupLatitude;} public void setPickupLatitude(Double v){pickupLatitude=v;}
    public Double getPickupLongitude(){return pickupLongitude;} public void setPickupLongitude(Double v){pickupLongitude=v;}
    public String getDropAddress(){return dropAddress;} public void setDropAddress(String v){dropAddress=v;}
    public String getDropPinCode(){return dropPinCode;} public void setDropPinCode(String v){dropPinCode=v;}
    public Double getDropLatitude(){return dropLatitude;} public void setDropLatitude(Double v){dropLatitude=v;}
    public Double getDropLongitude(){return dropLongitude;} public void setDropLongitude(Double v){dropLongitude=v;}
    public BigDecimal getDistanceKm(){return distanceKm;} public void setDistanceKm(BigDecimal v){distanceKm=v;}
    public LocalDate getPickupDate(){return pickupDate;} public void setPickupDate(LocalDate v){pickupDate=v;}
    public LocalTime getPickupTime(){return pickupTime;} public void setPickupTime(LocalTime v){pickupTime=v;}
    public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate v){returnDate=v;}
    public LocalTime getReturnTime(){return returnTime;} public void setReturnTime(LocalTime v){returnTime=v;}
    public int getPassengers(){return passengers;} public void setPassengers(int v){passengers=v;}
    public String getCustomerNote(){return customerNote;} public void setCustomerNote(String v){customerNote=v;}
    public BigDecimal getEstimatedFare(){return estimatedFare;} public void setEstimatedFare(BigDecimal v){estimatedFare=v;}
    public BigDecimal getOwnerQuotedFare(){return ownerQuotedFare;} public void setOwnerQuotedFare(BigDecimal v){ownerQuotedFare=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getOwnerResponseNote(){return ownerResponseNote;} public void setOwnerResponseNote(String v){ownerResponseNote=v;}
    public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
    public String getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(String v){paymentStatus=v;}
    public String getPaymentUtr(){return paymentUtr;} public void setPaymentUtr(String v){paymentUtr=v;}
    public String getPaymentProofUrl(){return paymentProofUrl;} public void setPaymentProofUrl(String v){paymentProofUrl=v;}
    public String getPaymentProofName(){return paymentProofName;} public void setPaymentProofName(String v){paymentProofName=v;}
    public String getPaymentReviewNote(){return paymentReviewNote;} public void setPaymentReviewNote(String v){paymentReviewNote=v;}
    public BigDecimal getAmountPaid(){return amountPaid;} public void setAmountPaid(BigDecimal v){amountPaid=v;}
    public Integer getStartOdometerKm(){return startOdometerKm;} public void setStartOdometerKm(Integer v){startOdometerKm=v;}
    public Integer getEndOdometerKm(){return endOdometerKm;} public void setEndOdometerKm(Integer v){endOdometerKm=v;}
    public BigDecimal getPlatformCommissionRate(){return platformCommissionRate;} public void setPlatformCommissionRate(BigDecimal v){platformCommissionRate=v;}
    public BigDecimal getPlatformCommissionAmount(){return platformCommissionAmount;} public void setPlatformCommissionAmount(BigDecimal v){platformCommissionAmount=v;}
    public String getCommissionStatus(){return commissionStatus;} public void setCommissionStatus(String v){commissionStatus=v;}
    public String getCommissionPaymentUtr(){return commissionPaymentUtr;} public void setCommissionPaymentUtr(String v){commissionPaymentUtr=v;}
    public String getCommissionPaymentProofUrl(){return commissionPaymentProofUrl;} public void setCommissionPaymentProofUrl(String v){commissionPaymentProofUrl=v;}
    public Instant getCompletedAt(){return completedAt;} public void setCompletedAt(Instant v){completedAt=v;}
    public Instant getCancelledAt(){return cancelledAt;} public void setCancelledAt(Instant v){cancelledAt=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
