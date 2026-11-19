package com.asmj.marketplace.rental;

import com.asmj.marketplace.delivery.GoogleRoutesService;
import com.asmj.marketplace.audit.AuditService;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import com.asmj.marketplace.discovery.MarketplaceModuleCategory;
import com.asmj.marketplace.discovery.MarketplaceModuleCategoryRepository;
import java.util.List;
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
    private final RentalSettingsRepository rentalSettings;
    private final AuditService audit;
    private final MarketplaceModuleCategoryRepository moduleCategories;
    @Value("${google.maps.browser-api-key:}") private String browserMapsKey;
    @Value("${google.maps.map-id:DEMO_MAP_ID}") private String browserMapsMapId;

    public RentalController(RentalVehicleRepository vehicles, RentalBookingRepository bookings,
                            UserAccountRepository users, GoogleRoutesService routes, CloudinaryImageService imageUploads, RentalSettingsRepository rentalSettings, AuditService audit, MarketplaceModuleCategoryRepository moduleCategories) {
        this.vehicles=vehicles; this.bookings=bookings; this.users=users; this.routes=routes; this.imageUploads=imageUploads; this.rentalSettings=rentalSettings; this.audit=audit; this.moduleCategories=moduleCategories;
    }

    @GetMapping("/rentals")
    public String browse(@RequestParam(required=false) String category, Model model) {
        var list=vehicles.findByStatusOrderByCreatedAtDesc("APPROVED").stream()
                .filter(RentalVehicle::isEligible)
                .filter(v -> category==null || category.isBlank() || category.equals(v.getVehicleCategory()))
                .toList();
        model.addAttribute("vehicles",list); model.addAttribute("selectedCategory",category==null?"":category);
        model.addAttribute("vehicleCategories",moduleCategories.findByTypeAndActiveTrueOrderByNameAsc("RENTAL"));
        return "rental/list";
    }

    @GetMapping("/rentals/{id}/book")
    public String bookingForm(@PathVariable String id, Principal principal, Model model) {
        UserAccount customer=current(principal);
        if(customer.getRole()!=Role.ROLE_CUSTOMER) return "auth/access-denied";
        RentalVehicle vehicle=vehicles.findById(id).filter(RentalVehicle::isEligible).orElseThrow(() -> new IllegalArgumentException("This vehicle is not currently available for booking."));
        model.addAttribute("vehicle",vehicle); model.addAttribute("customer",customer);
        model.addAttribute("googleMapsBrowserKey",browserMapsKey);
        model.addAttribute("googleMapsMapId",browserMapsMapId);
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
            b.setOwnerUserId(vehicle.getOwnerUserId()); b.setVehicleId(vehicle.getId()); b.setVehicleName(vehicle.getMakeModel()); b.setVehicleCategory(vehicle.getVehicleCategory()); b.setVehicleSubcategory(vehicle.getVehicleSubcategory());
            b.setTripType(tripType); b.setPickupAddress(pickupAddress.trim()); b.setPickupPinCode(pickupPinCode.trim()); b.setPickupLatitude(pickupLatitude); b.setPickupLongitude(pickupLongitude);
            b.setDropAddress(dropAddress.trim()); b.setDropPinCode(dropPinCode.trim()); b.setDropLatitude(dropLatitude); b.setDropLongitude(dropLongitude);
            b.setDistanceKm(distance); b.setPickupDate(pickupDate); b.setPickupTime(pickupTime); b.setReturnDate(returnDate); b.setReturnTime(returnTime);
            b.setPassengers(passengers); b.setCustomerNote(customerNote==null?"":customerNote.trim()); b.setEstimatedFare(estimate); b.setCreatedAt(Instant.now()); b.setUpdatedAt(Instant.now());
            bookings.save(b); audit.record(customer.getEmail(),"CUSTOMER","RENTAL_BOOKING_REQUESTED","RENTAL_BOOKING",b.getId(),b.getBookingNumber()); redirect.addFlashAttribute("successMessage","Rental request "+b.getBookingNumber()+" was sent to the vehicle owner. The owner must confirm availability and final fare.");
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
    public String newVehicle(Model model) { model.addAttribute("vehicleCategories",moduleCategories.findByTypeAndActiveTrueOrderByNameAsc("RENTAL")); return "vendor/rental/vehicle-form"; }

    @GetMapping("/api/rentals/categories") @ResponseBody
    public List<Map<String,Object>> rentalCategories() { return moduleCategories.findByTypeAndActiveTrueOrderByNameAsc("RENTAL").stream().map(c -> Map.<String,Object>of("id",c.getId(),"name",c.getName(),"subcategories",c.getActiveSubcategories())).toList(); }

    @PostMapping("/vendor/rentals")
    public String saveVehicle(Principal principal, @RequestParam String categoryId, @RequestParam String vehicleSubcategory, @RequestParam String makeModel,
            @RequestParam String registrationNumber, @RequestParam int seatingCapacity, @RequestParam(required=false) String description, @RequestParam(required=false) MultipartFile vehiclePhoto,
            @RequestParam String driverName, @RequestParam String driverMobile, @RequestParam String driverLicenceNumber,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate driverLicenceValidUntil,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate insuranceValidUntil,
            @RequestParam BigDecimal baseFare, @RequestParam BigDecimal perKmRate, RedirectAttributes redirect) {
        UserAccount owner=current(principal);
        try {
            if(owner.getRole()!=Role.ROLE_VENDOR) throw new IllegalArgumentException("An approved ASMJ Shoppe seller account is required to register a rental vehicle.");
            MarketplaceModuleCategory selectedCategory=moduleCategories.findById(categoryId).filter(c -> "RENTAL".equals(c.getType()) && c.isActive()).orElseThrow(() -> new IllegalArgumentException("Choose an active vehicle category."));
            if(vehicleSubcategory==null || vehicleSubcategory.isBlank() || selectedCategory.getSubcategories().stream().noneMatch(x -> x.equalsIgnoreCase(vehicleSubcategory.trim()))) throw new IllegalArgumentException("Choose a valid subcategory for the selected vehicle category.");
            if(driverMobile==null||!driverMobile.matches("[0-9+ -]{10,16}")) throw new IllegalArgumentException("Enter a valid driver mobile number.");
            if(makeModel==null||makeModel.isBlank()||registrationNumber==null||registrationNumber.isBlank()||driverName==null||driverName.isBlank()||driverLicenceNumber==null||driverLicenceNumber.isBlank()) throw new IllegalArgumentException("Complete all required vehicle and driver details.");
            if(vehicles.existsByRegistrationNumberIgnoreCase(registrationNumber.trim())) throw new IllegalArgumentException("This vehicle registration number is already registered.");
            if(seatingCapacity<1||seatingCapacity>60) throw new IllegalArgumentException("Enter a valid seating capacity between 1 and 60.");
            if(driverLicenceValidUntil==null||driverLicenceValidUntil.isBefore(LocalDate.now())||insuranceValidUntil==null||insuranceValidUntil.isBefore(LocalDate.now())) throw new IllegalArgumentException("Driver licence and vehicle insurance must be currently valid.");
            if(baseFare==null||perKmRate==null||baseFare.signum()<0||perKmRate.signum()<0||baseFare.compareTo(new BigDecimal("100000"))>0||perKmRate.compareTo(new BigDecimal("10000"))>0) throw new IllegalArgumentException("Enter valid base and per-kilometre rates.");
            RentalVehicle v=new RentalVehicle(); v.setOwnerUserId(owner.getId()); v.setOwnerName(owner.getFullName()); v.setVehicleCategory(selectedCategory.getName()); v.setVehicleSubcategory(vehicleSubcategory.trim()); v.setMakeModel(makeModel.trim()); v.setRegistrationNumber(registrationNumber.trim().toUpperCase()); v.setSeatingCapacity(seatingCapacity); v.setDescription(description==null?"":description.trim()); if(vehiclePhoto!=null&&!vehiclePhoto.isEmpty()){var uploaded=imageUploads.uploadProductImage(vehiclePhoto);v.setImageUrl(String.valueOf(uploaded.get("secure_url")));} v.setDriverName(driverName.trim()); v.setDriverMobile(driverMobile.trim()); v.setDriverLicenceNumber(driverLicenceNumber.trim()); v.setDriverLicenceValidUntil(driverLicenceValidUntil); v.setInsuranceValidUntil(insuranceValidUntil); v.setBaseFare(baseFare); v.setPerKmRate(perKmRate); v.setStatus("PENDING"); v.setCreatedAt(Instant.now()); v.setUpdatedAt(Instant.now()); vehicles.save(v); audit.record(owner.getEmail(),"VENDOR","RENTAL_VEHICLE_SUBMITTED","RENTAL_VEHICLE",v.getId(),v.getRegistrationNumber());
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
        if("ACCEPT".equals(decision)){ if(ownerQuotedFare==null||ownerQuotedFare.signum()<=0||ownerQuotedFare.compareTo(new BigDecimal("10000000"))>0) throw new IllegalArgumentException("Enter a valid final fare before accepting."); if(hasConfirmedOverlap(b)) throw new IllegalArgumentException("This vehicle already has a confirmed booking for the selected date/time."); b.setOwnerQuotedFare(ownerQuotedFare.setScale(2,RoundingMode.HALF_UP)); b.setStatus("QUOTED"); } else if("REJECT".equals(decision)) b.setStatus("REJECTED"); else throw new IllegalArgumentException("Invalid booking decision.");
        b.setOwnerResponseNote(note==null?"":note.trim()); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(owner.getEmail(),"VENDOR","RENTAL_BOOKING_"+b.getStatus(),"RENTAL_BOOKING",b.getId(),b.getBookingNumber()); redirect.addFlashAttribute("successMessage","Rental booking updated."); return "redirect:/vendor/rentals/bookings";
    }

    @GetMapping("/admin/rentals")
    public String adminRentals(Model model) { model.addAttribute("vehicles",vehicles.findAll()); model.addAttribute("bookings",bookings.findAll()); model.addAttribute("rentalSettings",rentalSettings.findById("default").orElseGet(RentalSettings::new)); return "admin/rental/index"; }

    @PostMapping("/admin/rentals/vehicles/{id}/decision")
    public String adminVehicleDecision(@PathVariable String id, @RequestParam String decision, @RequestParam(required=false) String note, RedirectAttributes redirect) {
        RentalVehicle v=vehicles.findById(id).orElseThrow(); if("APPROVE".equals(decision)){v.setStatus("APPROVED");v.setAdminNote("");}else if("REJECT".equals(decision)){v.setStatus("REJECTED");v.setAdminNote(note==null?"Rejected by admin":note.trim());}else if("SUSPEND".equals(decision)){v.setStatus("SUSPENDED");v.setAdminNote(note==null?"Suspended by admin":note.trim());}else throw new IllegalArgumentException("Invalid vehicle decision."); v.setUpdatedAt(Instant.now()); vehicles.save(v); audit.record("admin","ADMIN","RENTAL_VEHICLE_"+v.getStatus(),"RENTAL_VEHICLE",id,v.getRegistrationNumber()); redirect.addFlashAttribute("successMessage","Rental vehicle status updated."); return "redirect:/admin/rentals";
    }

    @PostMapping("/customer/rentals/{id}/quote/accept")
    public String acceptQuote(@PathVariable String id, Principal principal, RedirectAttributes ra) {
        UserAccount customer=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getCustomerUserId().equals(customer.getId())).orElseThrow();
        if(!"QUOTED".equals(b.getStatus())) { ra.addFlashAttribute("errorMessage","This booking has no pending owner quote."); return "redirect:/customer/rentals"; }
        if(hasConfirmedOverlap(b)) { ra.addFlashAttribute("errorMessage","The vehicle is no longer available for this time. Please contact the owner."); return "redirect:/customer/rentals"; }
        b.setStatus("CONFIRMED"); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(customer.getEmail(),"CUSTOMER","RENTAL_QUOTE_ACCEPTED","RENTAL_BOOKING",id,b.getBookingNumber()); ra.addFlashAttribute("successMessage","Owner quote accepted. Your rental is confirmed; arrange payment with the vehicle owner."); return "redirect:/customer/rentals";
    }

    @PostMapping("/customer/rentals/{id}/cancel")
    public String cancelCustomerBooking(@PathVariable String id, Principal principal, @RequestParam(required=false) String reason, RedirectAttributes ra) {
        UserAccount customer=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getCustomerUserId().equals(customer.getId())).orElseThrow();
        if(!java.util.Set.of("REQUESTED","QUOTED","CONFIRMED").contains(b.getStatus())) { ra.addFlashAttribute("errorMessage","This booking can no longer be cancelled online. Contact support."); return "redirect:/customer/rentals"; }
        b.setStatus("CANCELLED"); b.setCancelledAt(Instant.now()); b.setOwnerResponseNote("Customer cancellation: "+(reason==null?"":reason.trim())); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(customer.getEmail(),"CUSTOMER","RENTAL_BOOKING_CANCELLED","RENTAL_BOOKING",id,b.getBookingNumber()); ra.addFlashAttribute("successMessage","Rental booking cancelled."); return "redirect:/customer/rentals";
    }

    @PostMapping("/customer/rentals/{id}/payment")
    public String submitRentalPayment(@PathVariable String id, Principal principal, @RequestParam String paymentMethod, @RequestParam(required=false) String utr, @RequestParam(required=false) BigDecimal amountPaid, @RequestParam(required=false) MultipartFile proof, RedirectAttributes ra) {
        UserAccount customer=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getCustomerUserId().equals(customer.getId())).orElseThrow();
        try { if(!"CONFIRMED".equals(b.getStatus())) throw new IllegalArgumentException("Payment can be submitted after the owner quote is accepted.");
            if("UPI".equals(paymentMethod)){ if(amountPaid==null||amountPaid.signum()<=0||amountPaid.compareTo(b.getOwnerQuotedFare())!=0) throw new IllegalArgumentException("Enter the exact owner-confirmed fare amount."); b.setAmountPaid(amountPaid.setScale(2,RoundingMode.HALF_UP)); if(utr==null||!utr.trim().matches("[A-Za-z0-9]{8,30}")) throw new IllegalArgumentException("Enter a valid UPI UTR/reference."); if(bookings.findAll().stream().anyMatch(x->!x.getId().equals(id)&&utr.trim().equalsIgnoreCase(x.getPaymentUtr()))) throw new IllegalArgumentException("This UTR/reference has already been used for another rental booking."); if(proof==null||proof.isEmpty()) throw new IllegalArgumentException("Upload the UPI payment screenshot."); var f=imageUploads.uploadDocument(proof,"asmj-marketplace/rentals/payment-proofs"); b.setPaymentUtr(utr.trim()); b.setPaymentProofUrl(String.valueOf(f.get("secure_url"))); b.setPaymentProofName(proof.getOriginalFilename()); b.setPaymentStatus("PROOF_SUBMITTED"); }
            else if("CASH".equals(paymentMethod)){ b.setPaymentMethod("CASH"); b.setPaymentStatus("PAY_ON_TRIP"); }
            else throw new IllegalArgumentException("Choose UPI or cash."); b.setPaymentMethod(paymentMethod); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(customer.getEmail(),"CUSTOMER","RENTAL_PAYMENT_SUBMITTED","RENTAL_BOOKING",id,paymentMethod); ra.addFlashAttribute("successMessage","Rental payment details submitted to the vehicle owner.");
        } catch(IllegalArgumentException|IllegalStateException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());} return "redirect:/customer/rentals";
    }

    @PostMapping("/vendor/rentals/bookings/{id}/payment/verify")
    public String verifyRentalPayment(@PathVariable String id, Principal principal, @RequestParam String decision, @RequestParam(required=false) String note, RedirectAttributes ra) {
        UserAccount owner=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getOwnerUserId().equals(owner.getId())).orElseThrow();
        if(!"PROOF_SUBMITTED".equals(b.getPaymentStatus())) {ra.addFlashAttribute("errorMessage","No UPI proof is awaiting verification.");return "redirect:/vendor/rentals/bookings";}
        if("VERIFY".equals(decision)) b.setPaymentStatus("VERIFIED"); else if("REJECT".equals(decision)){b.setPaymentStatus("REJECTED");b.setPaymentReviewNote(note==null?"Payment not verified":note.trim());} else throw new IllegalArgumentException("Invalid payment decision.");
        b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(owner.getEmail(),"VENDOR","RENTAL_PAYMENT_"+b.getPaymentStatus(),"RENTAL_BOOKING",id,b.getBookingNumber()); ra.addFlashAttribute("successMessage","Rental payment review updated."); return "redirect:/vendor/rentals/bookings";
    }

    @PostMapping("/vendor/rentals/bookings/{id}/trip/start")
    public String startRentalTrip(@PathVariable String id, Principal principal, @RequestParam int startOdometerKm, RedirectAttributes ra) {
        UserAccount owner=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getOwnerUserId().equals(owner.getId())).orElseThrow();
        if(!"CONFIRMED".equals(b.getStatus())) throw new IllegalArgumentException("Only confirmed bookings can be started.");
        if(startOdometerKm<1) throw new IllegalArgumentException("Enter a valid starting odometer reading.");
        if("UPI".equals(b.getPaymentMethod())&& !"VERIFIED".equals(b.getPaymentStatus())) throw new IllegalArgumentException("Verify the UPI payment before starting the trip.");
        b.setStartOdometerKm(startOdometerKm); b.setStatus("IN_PROGRESS"); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(owner.getEmail(),"VENDOR","RENTAL_TRIP_STARTED","RENTAL_BOOKING",id,"Start odometer: "+startOdometerKm); ra.addFlashAttribute("successMessage","Rental trip started."); return "redirect:/vendor/rentals/bookings";
    }

    @PostMapping("/vendor/rentals/bookings/{id}/trip/complete")
    public String completeRentalTrip(@PathVariable String id, Principal principal, @RequestParam int endOdometerKm, @RequestParam(required=false) BigDecimal cashReceived, RedirectAttributes ra) {
        UserAccount owner=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getOwnerUserId().equals(owner.getId())).orElseThrow();
        if(!"IN_PROGRESS".equals(b.getStatus())||b.getStartOdometerKm()==null||endOdometerKm<=b.getStartOdometerKm()) throw new IllegalArgumentException("Final odometer must be greater than the starting reading.");
        if("CASH".equals(b.getPaymentMethod())) { if(cashReceived==null||cashReceived.signum()<=0||b.getOwnerQuotedFare()==null||cashReceived.compareTo(b.getOwnerQuotedFare())!=0) throw new IllegalArgumentException("Enter the exact owner-confirmed fare collected in cash."); b.setAmountPaid(cashReceived.setScale(2,RoundingMode.HALF_UP)); b.setPaymentStatus("VERIFIED"); }
        if(!"VERIFIED".equals(b.getPaymentStatus())) throw new IllegalArgumentException("Verify full rental payment before completing the trip.");
        b.setEndOdometerKm(endOdometerKm); b.setStatus("COMPLETED"); b.setCompletedAt(Instant.now());
        RentalSettings settings=rentalSettings.findById("default").orElseGet(RentalSettings::new); BigDecimal rate=settings.getCommissionRatePercent()==null?new BigDecimal("5.00"):settings.getCommissionRatePercent();
        BigDecimal paid=b.getAmountPaid()==null?BigDecimal.ZERO:b.getAmountPaid(); b.setPlatformCommissionRate(rate); b.setPlatformCommissionAmount(paid.multiply(rate).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP)); b.setCommissionStatus(b.getPlatformCommissionAmount().signum()>0?"DUE":"NOT_DUE");
        b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(owner.getEmail(),"VENDOR","RENTAL_TRIP_COMPLETED","RENTAL_BOOKING",id,"Odometer "+b.getStartOdometerKm()+" to "+endOdometerKm+"; commission ₹"+b.getPlatformCommissionAmount()); ra.addFlashAttribute("successMessage","Trip completed. Rental commission has been added to the separate rental settlement ledger."); return "redirect:/vendor/rentals/bookings";
    }

    @PostMapping("/admin/rentals/settings")
    public String saveRentalSettings(@RequestParam BigDecimal commissionRatePercent, Principal principal, RedirectAttributes ra) {
        if(commissionRatePercent==null||commissionRatePercent.signum()<0||commissionRatePercent.compareTo(new BigDecimal("30"))>0) throw new IllegalArgumentException("Rental commission must be between 0 and 30 percent.");
        RentalSettings s=rentalSettings.findById("default").orElseGet(RentalSettings::new); s.setCommissionRatePercent(commissionRatePercent.setScale(2,RoundingMode.HALF_UP)); rentalSettings.save(s); audit.record(principal.getName(),"ADMIN","RENTAL_COMMISSION_RATE_UPDATED","RENTAL_SETTINGS","default",s.getCommissionRatePercent().toString()); ra.addFlashAttribute("successMessage","Rental commission rate saved. It applies to trips completed after this change."); return "redirect:/admin/rentals";
    }

    @PostMapping("/vendor/rentals/commission/{id}/submit")
    public String submitRentalCommission(@PathVariable String id, Principal principal, @RequestParam String utr, @RequestParam MultipartFile proof, RedirectAttributes ra) {
        UserAccount owner=current(principal); RentalBooking b=bookings.findById(id).filter(x->x.getOwnerUserId().equals(owner.getId())).orElseThrow();
        if(!"DUE".equals(b.getCommissionStatus())) throw new IllegalArgumentException("No rental commission is due for this booking.");
        if(utr==null||!utr.trim().matches("[A-Za-z0-9]{8,30}")) throw new IllegalArgumentException("Enter a valid UTR/reference."); var f=imageUploads.uploadDocument(proof,"asmj-marketplace/rentals/commission-proofs"); b.setCommissionPaymentUtr(utr.trim()); b.setCommissionPaymentProofUrl(String.valueOf(f.get("secure_url"))); b.setCommissionStatus("SUBMITTED"); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(owner.getEmail(),"VENDOR","RENTAL_COMMISSION_SUBMITTED","RENTAL_BOOKING",id,utr); ra.addFlashAttribute("successMessage","Rental commission proof submitted for admin verification."); return "redirect:/vendor/rentals/bookings";
    }

    @PostMapping("/admin/rentals/commission/{id}/verify")
    public String verifyRentalCommission(@PathVariable String id, Principal principal, RedirectAttributes ra) {
        RentalBooking b=bookings.findById(id).orElseThrow(); if(!"SUBMITTED".equals(b.getCommissionStatus())) throw new IllegalArgumentException("No rental commission payment is awaiting verification."); b.setCommissionStatus("PAID"); b.setUpdatedAt(Instant.now()); bookings.save(b); audit.record(principal.getName(),"ADMIN","RENTAL_COMMISSION_VERIFIED","RENTAL_BOOKING",id,b.getCommissionPaymentUtr()); ra.addFlashAttribute("successMessage","Rental commission payment verified."); return "redirect:/admin/rentals";
    }

    private boolean hasConfirmedOverlap(RentalBooking candidate) {
        return bookings.findByVehicleIdOrderByCreatedAtDesc(candidate.getVehicleId()).stream().filter(b->!b.getId().equals(candidate.getId()))
            .filter(b->java.util.Set.of("CONFIRMED","IN_PROGRESS").contains(b.getStatus()))
            .anyMatch(b->{LocalDate aStart=candidate.getPickupDate(), aEnd=candidate.getReturnDate()==null?candidate.getPickupDate():candidate.getReturnDate(); LocalDate bStart=b.getPickupDate(), bEnd=b.getReturnDate()==null?b.getPickupDate():b.getReturnDate(); return aStart!=null&&aEnd!=null&&bStart!=null&&bEnd!=null&&!aStart.isAfter(bEnd)&&!bStart.isAfter(aEnd);});
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
