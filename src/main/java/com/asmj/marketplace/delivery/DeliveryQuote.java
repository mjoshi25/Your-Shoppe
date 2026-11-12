package com.asmj.marketplace.delivery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class DeliveryQuote implements Serializable {
    private String vendorUserId, addressFingerprint, originAddress, encodedPolyline;
    private Double originLatitude, originLongitude, destinationLatitude, destinationLongitude;
    private BigDecimal distanceKm, freeDistanceKm, ratePerKm, charge;
    private int chargeableKm;
    private Instant createdAt = Instant.now();
    public String getVendorUserId(){return vendorUserId;} public void setVendorUserId(String v){vendorUserId=v;}
    public String getAddressFingerprint(){return addressFingerprint;} public void setAddressFingerprint(String v){addressFingerprint=v;}
    public String getOriginAddress(){return originAddress;} public void setOriginAddress(String v){originAddress=v;}
    public String getEncodedPolyline(){return encodedPolyline;} public void setEncodedPolyline(String v){encodedPolyline=v;}
    public Double getOriginLatitude(){return originLatitude;} public void setOriginLatitude(Double v){originLatitude=v;}
    public Double getOriginLongitude(){return originLongitude;} public void setOriginLongitude(Double v){originLongitude=v;}
    public Double getDestinationLatitude(){return destinationLatitude;} public void setDestinationLatitude(Double v){destinationLatitude=v;}
    public Double getDestinationLongitude(){return destinationLongitude;} public void setDestinationLongitude(Double v){destinationLongitude=v;}
    public BigDecimal getDistanceKm(){return distanceKm;} public void setDistanceKm(BigDecimal v){distanceKm=v;}
    public BigDecimal getFreeDistanceKm(){return freeDistanceKm;} public void setFreeDistanceKm(BigDecimal v){freeDistanceKm=v;}
    public BigDecimal getRatePerKm(){return ratePerKm;} public void setRatePerKm(BigDecimal v){ratePerKm=v;}
    public BigDecimal getCharge(){return charge;} public void setCharge(BigDecimal v){charge=v;}
    public int getChargeableKm(){return chargeableKm;} public void setChargeableKm(int v){chargeableKm=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
