package com.asmj.marketplace.rental;

import com.asmj.marketplace.delivery.GoogleRoutesService;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RentalController {
    private final RentalVehicleRepository vehicles;
    private final RentalBookingRepository bookings;
    private final UserAccountRepository users;
    private final GoogleRoutesService routes;
    private final CloudinaryImageService imageUploads;
    @Value("${google.maps.browser-api-key:}") private String browserMapsKey;

    public RentalController(RentalVehicleRepository vehicles, RentalBookingRepository bookings,
                            UserAccountRepository users, GoogleRoutesService routes, CloudinaryImageService imageUploads) {
        this.vehicles=vehicles; this.bookings=bookings; this.users=users; this.routes=routes; this.imageUploads=imageUploads;
    }

    @GetMapping("/rentals")
    public String browse(@RequestParam(required=false) String category, Model model) {
        var list=vehicles.findByStatusOrderByCreatedAtDesc("APPROVED").stream()
                .filter(RentalVehicle::isEligible)
                .filter(v -> category==null || category.isBlank() || category.equals(v.getVehicleCategory()))
                .toList();
        model.addAttribute("vehicles",list); model.addAttribute("selectedCategory",category==null?"":category);
        return "rental/list";
    }

    @GetMapping("/rentals/{id}/book")
    public String bookingForm(@PathVariable String id, Principal principal, Model model) {
        UserAccount customer=current(principal);
        if(customer.getRole()!=Role.ROLE_CUSTOMER) return "auth/access-denied";
        RentalVehicle vehicle=vehicles.findById(id).filter(RentalVehicle::isEligible).orElseThrow(() -> new IllegalArgumentException("This vehicle is not currently available for booking."));
        model.addAttribute("vehicle",vehicle); model.addAttribute("customer",customer);
        model.addAttribute("googleMapsBrowserKey",browserMapsKey);
        model.addAttribute("minimumDate",LocalDate.now().plusDays(1));
        return "rental/book";
    }

    @PostMapping("/rentals/{id}/book")
    public String submitBooking(@PathVariable String id, Principal principal,
            @RequestParam String tripType, @RequestParam String pickupAddress, @RequestParam String pickupPinCode,
            @RequestParam Double pickupLatitude, @RequestParam Double pickupLongitude,
            @RequestParam String dropAddress, @RequestParam String dropPinCode,
            @RequestParam Double dropLatitude, @RequestParam Double dropLongitude,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime pickupTime,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime returnTime,
            @RequestParam int passengers, @RequestParam(required=false) String customerNote,
            RedirectAttributes redirect) {
        UserAccount customer=current(principal);
        if(customer.getRole()!=Role.ROLE_CUSTOMER) return "auth/access-denied";
        RentalVehicle vehicle=vehicles.findById(id).filter(RentalVehicle::isEligible).orElseThrow(() -> new IllegalArgumentException("This vehicle is not currently available for booking."));
        try {
            validatePin(pickupPinCode); validatePin(dropPinCode);
            if(pickupAddress.isBlank() || dropAddress.isBlank()) throw new IllegalArgumentException("Choose both pickup and drop locations on the map.");
            if(!validCoordinate(pickupLatitude,-90,90)||!validCoordinate(dropLatitude,-90,90)||!validCoordinate(pickupLongitude,-180,180)||!validCoordinate(dropLongitude,-180,180)) throw new IllegalArgumentException("Select valid pickup and drop map pins.");
            if(!pickupDate.isAfter(LocalDate.now(ZoneId.of("Asia/Kolkata")))) throw new IllegalArgumentException("Pickup date must be at least tomorrow.");
            if(pickupTime==null) throw new IllegalArgumentException("Select a pickup time.");
            if(passengers<1 || passengers>vehicle.getSeatingCapacity()) throw new IllegalArgumentException("Passenger count must be between 1 and the vehicle seating capacity.");
            if(!"ONE_WAY".equals(tripType) && !"ROUND_TRIP".equals(tripType)) throw new IllegalArgumentException("Choose one-way or round trip.");
            if("ROUND_TRIP".equals(tripType) && (returnDate==null || returnTime==null || returnDate.isBefore(pickupDate) || (returnDate.equals(pickupDate)&&!returnTime.isAfter(pickupTime)))) throw new IllegalArgumentException("Round-trip return date and time must be after pickup.");
            var distance=routes.drivingDistanceKm(pickupLatitude,pickupLongitude,dropLatitude,dropLongitude);
            BigDecimal multiplier="ROUND_TRIP".equals(tripType)?new BigDecimal("2"):BigDecimal.ONE;
            BigDecimal estimate=vehicle.getBaseFare().add(vehicle.getPerKmRate().multiply(distance).multiply(multiplier)).setScale(2,RoundingMode.HALF_UP);
            RentalBooking b=new RentalBooking(); b.setBookingNumber("RNT-"+UUID.randomUUID().toString().substring(0,8).toUpperCase());
            b.setCustomerUserId(customer.getId()); b.setCustomerName(customer.getFullName()); b.setCustomerMobile(customer.getMobile());
            b.setOwnerUserId(vehicle.getOwnerUserId()); b.setVehicleId(vehicle.getId()); b.setVehicleName(vehicle.getMakeModel()); b.setVehicleCategory(vehicle.getVehicleCategory());
            b.setTripType(tripType); b.setPickupAddress(pickupAddress.trim()); b.setPickupPinCode(pickupPinCode.trim()); b.setPickupLatitude(pickupLatitude); b.setPickupLongitude(pickupLongitude);
            b.setDropAddress(dropAddress.trim()); b.setDropPinCode(dropPinCode.trim()); b.setDropLatitude(dropLatitude); b.setDropLongitude(dropLongitude);
            b.setDistanceKm(distance); b.setPickupDate(pickupDate); b.setPickupTime(pickupTime); b.setReturnDate(returnDate); b.setReturnTime(returnTime);
            b.setPassengers(passengers); b.setCustomerNote(customerNote==null?"":customerNote.trim()); b.setEstimatedFare(estimate); b.setCreatedAt(Instant.now()); b.setUpdatedAt(Instant.now());
            bookings.save(b); redirect.addFlashAttribute("successMessage","Rental request "+b.getBookingNumber()+" was sent to the vehicle owner. The owner must confirm availability and final fare.");
            return "redirect:/customer/rentals";
        } catch(IllegalArgumentException|IllegalStateException ex) {
            redirect.addFlashAttribute("errorMessage",ex.getMessage()); return "redirect:/rentals/"+id+"/book";
        }
    }

    @GetMapping("/customer/rentals")
    public String customerBookings(Principal principal, Model model) { UserAccount u=current(principal); if(u.getRole()!=Role.ROLE_CUSTOMER)return "auth/access-denied"; model.addAttribute("bookings",bookings.findByCustomerUserIdOrderByCreatedAtDesc(u.getId())); return "rental/customer-bookings"; }

    @GetMapping("/vendor/rentals")
    public String ownerVehicles(Principal principal, Model model) { UserAccount u=current(principal); model.addAttribute("vehicles",vehicles.findByOwnerUserIdOrderByCreatedAtDesc(u.getId())); return "vendor/rental/vehicles"; }

    @GetMapping("/vendor/rentals/new")
    public String newVehicle() { return "vendor/rental/vehicle-form"; }

    @PostMapping("/vendor/rentals")
    public String saveVehicle(Principal principal, @RequestParam String vehicleCategory, @RequestParam String makeModel,
            @RequestParam String registrationNumber, @RequestParam int seatingCapacity, @RequestParam(required=false) String description, @RequestParam(required=false) MultipartFile vehiclePhoto,
            @RequestParam String driverName, @RequestParam String driverMobile, @RequestParam String driverLicenceNumber,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate driverLicenceValidUntil,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate insuranceValidUntil,
            @RequestParam BigDecimal baseFare, @RequestParam BigDecimal perKmRate, RedirectAttributes redirect) {
        UserAccount owner=current(principal);
        try {
            if(owner.getRole()!=Role.ROLE_VENDOR) throw new IllegalArgumentException("An approved ASMJ Shoppe seller account is required to register a rental vehicle.");
            if(!java.util.Set.of("HATCHBACK","SEDAN","SUV","MUV","TEMPO_TRAVELLER","BUS").contains(vehicleCategory)) throw new IllegalArgumentException("Choose a supported vehicle category.");
            if(driverMobile==null||!driverMobile.matches("[0-9+ -]{10,16}")) throw new IllegalArgumentException("Enter a valid driver mobile number.");
            if(makeModel==null||makeModel.isBlank()||registrationNumber==null||registrationNumber.isBlank()||driverName==null||driverName.isBlank()||driverLicenceNumber==null||driverLicenceNumber.isBlank()) throw new IllegalArgumentException("Complete all required vehicle and driver details.");
            if(seatingCapacity<1||seatingCapacity>60) throw new IllegalArgumentException("Enter a valid seating capacity between 1 and 60.");
            if(driverLicenceValidUntil==null||driverLicenceValidUntil.isBefore(LocalDate.now())||insuranceValidUntil==null||insuranceValidUntil.isBefore(LocalDate.now())) throw new IllegalArgumentException("Driver licence and vehicle insurance must be currently valid.");
            if(baseFare==null||perKmRate==null||baseFare.signum()<0||perKmRate.signum()<0||baseFare.compareTo(new BigDecimal("100000"))>0||perKmRate.compareTo(new BigDecimal("10000"))>0) throw new IllegalArgumentException("Enter valid base and per-kilometre rates.");
            RentalVehicle v=new RentalVehicle(); v.setOwnerUserId(owner.getId()); v.setOwnerName(owner.getFullName()); v.setVehicleCategory(vehicleCategory); v.setMakeModel(makeModel.trim()); v.setRegistrationNumber(registrationNumber.trim().toUpperCase()); v.setSeatingCapacity(seatingCapacity); v.setDescription(description==null?"":description.trim()); if(vehiclePhoto!=null&&!vehiclePhoto.isEmpty()){var uploaded=imageUploads.uploadProductImage(vehiclePhoto);v.setImageUrl(String.valueOf(uploaded.get("secure_url")));} v.setDriverName(driverName.trim()); v.setDriverMobile(driverMobile.trim()); v.setDriverLicenceNumber(driverLicenceNumber.trim()); v.setDriverLicenceValidUntil(driverLicenceValidUntil); v.setInsuranceValidUntil(insuranceValidUntil); v.setBaseFare(baseFare); v.setPerKmRate(perKmRate); v.setStatus("PENDING"); v.setCreatedAt(Instant.now()); v.setUpdatedAt(Instant.now()); vehicles.save(v);
            redirect.addFlashAttribute("successMessage","Vehicle submitted for admin verification. It will appear to customers after approval.");
        } catch(IllegalArgumentException|IllegalStateException ex){redirect.addFlashAttribute("errorMessage",ex.getMessage()); return "redirect:/vendor/rentals/new";}
        return "redirect:/vendor/rentals";
    }

    @GetMapping("/vendor/rentals/bookings")
    public String ownerBookings(Principal principal, Model model) { UserAccount u=current(principal); model.addAttribute("bookings",bookings.findByOwnerUserIdOrderByCreatedAtDesc(u.getId())); return "vendor/rental/bookings"; }

    @PostMapping("/vendor/rentals/bookings/{id}/decision")
    public String ownerDecision(@PathVariable String id, @RequestParam String decision, @RequestParam(required=false) String note, @RequestParam(required=false) BigDecimal ownerQuotedFare,
                                Principal principal, RedirectAttributes redirect) {
        UserAccount owner=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getOwnerUserId().equals(owner.getId())).orElseThrow();
        if(!"REQUESTED".equals(b.getStatus())) {redirect.addFlashAttribute("errorMessage","Only new requests can be accepted or rejected."); return "redirect:/vendor/rentals/bookings";}
        if("ACCEPT".equals(decision)){ if(ownerQuotedFare==null||ownerQuotedFare.signum()<0||ownerQuotedFare.compareTo(new BigDecimal("10000000"))>0) throw new IllegalArgumentException("Enter a valid final fare before accepting."); b.setOwnerQuotedFare(ownerQuotedFare.setScale(2,RoundingMode.HALF_UP)); b.setStatus("ACCEPTED"); } else if("REJECT".equals(decision)) b.setStatus("REJECTED"); else throw new IllegalArgumentException("Invalid booking decision.");
        b.setOwnerResponseNote(note==null?"":note.trim()); b.setUpdatedAt(Instant.now()); bookings.save(b); redirect.addFlashAttribute("successMessage","Rental booking updated."); return "redirect:/vendor/rentals/bookings";
    }

    @GetMapping("/admin/rentals")
    public String adminRentals(Model model) { model.addAttribute("vehicles",vehicles.findAll()); model.addAttribute("bookings",bookings.findAll()); return "admin/rental/index"; }

    @PostMapping("/admin/rentals/vehicles/{id}/decision")
    public String adminVehicleDecision(@PathVariable String id, @RequestParam String decision, @RequestParam(required=false) String note, RedirectAttributes redirect) {
        RentalVehicle v=vehicles.findById(id).orElseThrow(); if("APPROVE".equals(decision)){v.setStatus("APPROVED");v.setAdminNote("");}else if("REJECT".equals(decision)){v.setStatus("REJECTED");v.setAdminNote(note==null?"Rejected by admin":note.trim());}else if("SUSPEND".equals(decision)){v.setStatus("SUSPENDED");v.setAdminNote(note==null?"Suspended by admin":note.trim());}else throw new IllegalArgumentException("Invalid vehicle decision."); v.setUpdatedAt(Instant.now()); vehicles.save(v); redirect.addFlashAttribute("successMessage","Rental vehicle status updated."); return "redirect:/admin/rentals";
    }

    @GetMapping("/api/rentals/quote") @ResponseBody
    public Map<String,Object> quote(@RequestParam String vehicleId, @RequestParam Double pickupLatitude, @RequestParam Double pickupLongitude, @RequestParam Double dropLatitude, @RequestParam Double dropLongitude, @RequestParam String tripType, Principal principal) {
        QuoteRequest request = new QuoteRequest(vehicleId,pickupLatitude,pickupLongitude,dropLatitude,dropLongitude,tripType);
        UserAccount customer=current(principal); if(customer.getRole()!=Role.ROLE_CUSTOMER) throw new IllegalArgumentException("Customer login is required to calculate a rental quote.");
        RentalVehicle vehicle=vehicles.findById(request.vehicleId()).filter(RentalVehicle::isEligible).orElseThrow(()->new IllegalArgumentException("This vehicle is not available."));
        if(!validCoordinate(request.pickupLatitude(),-90,90)||!validCoordinate(request.dropLatitude(),-90,90)||!validCoordinate(request.pickupLongitude(),-180,180)||!validCoordinate(request.dropLongitude(),-180,180)) throw new IllegalArgumentException("Select both map locations first.");
        var distance=routes.drivingDistanceKm(request.pickupLatitude(),request.pickupLongitude(),request.dropLatitude(),request.dropLongitude());
        BigDecimal multiplier="ROUND_TRIP".equals(request.tripType())?new BigDecimal("2"):BigDecimal.ONE;
        BigDecimal fare=vehicle.getBaseFare().add(vehicle.getPerKmRate().multiply(distance).multiply(multiplier)).setScale(2,RoundingMode.HALF_UP);
        return Map.of("distanceKm",distance,"estimatedFare",fare,"currency","INR","note","Indicative estimate only. Owner confirms final availability and fare.");
    }

    private UserAccount current(Principal principal) { return users.findByEmailIgnoreCase(principal.getName()).filter(UserAccount::isEnabled).orElseThrow(); }
    private boolean validCoordinate(Double v,double min,double max){return v!=null&&Double.isFinite(v)&&v>=min&&v<=max;}
    private void validatePin(String pin){if(pin==null||!pin.matches("[0-9]{6}"))throw new IllegalArgumentException("Enter a valid six-digit pickup and drop PIN code.");}
    public record QuoteRequest(String vehicleId, Double pickupLatitude, Double pickupLongitude, Double dropLatitude, Double dropLongitude, String tripType) {}
}
