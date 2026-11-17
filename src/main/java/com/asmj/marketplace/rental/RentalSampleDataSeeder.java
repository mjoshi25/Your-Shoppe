package com.asmj.marketplace.rental;

import com.asmj.marketplace.discovery.MarketplaceModuleCategory;
import com.asmj.marketplace.discovery.MarketplaceModuleCategoryRepository;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Opt-in, idempotent rental demo-data seeder. It never approves seeded vehicles. */
@Component
@Order(3)
public class RentalSampleDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RentalSampleDataSeeder.class);
    private static final String BATCH = "ASMJ_SHOPPE_RENTAL_DEMO_V1";
    private static final List<VendorSeed> VENDORS = List.of(
            new VendorSeed("shop2@asmj.co.in", "ASMJ Rental Demo Shop 2", "+919900000002", "Shop 2 Rental Services"),
            new VendorSeed("shop3@asmj.co.in", "ASMJ Rental Demo Shop 3", "+919900000003", "Shop 3 Mobility Rentals")
    );
    private static final String[][] VEHICLES = {
            {"Honda Activa 6G", "TVS Jupiter", "Royal Enfield Classic 350", "Bajaj Pulsar 150", "Ather 450X"},
            {"Bajaj Compact RE", "Mahindra Treo", "Piaggio Ape Xtra", "TVS King Deluxe", "Mahindra Alfa Plus"},
            {"Maruti Swift", "Honda Amaze", "Hyundai Creta", "Toyota Innova Crysta", "Toyota Fortuner"},
            {"Force Traveller 12-seater", "Force Traveller 17-seater", "Tata Winger", "Mini Coach 25-seater", "Volvo Coach 40-seater"},
            {"Mahindra Bolero Pickup", "Tata Ace Gold", "Ashok Leyland Dost", "Tata 407 Truck", "Eicher Cargo Truck"}
    };
    private static final int[][] CAPACITY = {{2,2,2,2,2},{3,4,3,4,3},{4,4,6,7,7},{12,17,12,25,40},{2,2,2,3,3}};

    private final UserAccountRepository users;
    private final VendorProfileRepository profiles;
    private final RentalVehicleRepository vehicles;
    private final MarketplaceModuleCategoryRepository categories;
    private final PasswordEncoder encoder;
    @Value("${app.seed.rentals.create:false}") private boolean createEnabled;
    @Value("${app.seed.rentals.delete:false}") private boolean deleteEnabled;
    @Value("${app.seed.rentals.vendor-password:}") private String vendorPassword;

    public RentalSampleDataSeeder(UserAccountRepository users, VendorProfileRepository profiles,
            RentalVehicleRepository vehicles, MarketplaceModuleCategoryRepository categories, PasswordEncoder encoder) {
        this.users = users; this.profiles = profiles; this.vehicles = vehicles; this.categories = categories; this.encoder = encoder;
    }

    @Override public void run(String... args) {
        if (!createEnabled && !deleteEnabled) return;
        if (deleteEnabled) { cleanup(); if (createEnabled) log.warn("Both rental seed flags are true; cleanup took precedence."); return; }
        if (VENDORS.stream().anyMatch(v -> !users.existsByEmailIgnoreCase(v.email())) && !StringUtils.hasText(vendorPassword)) {
            log.warn("Rental sample seeding skipped: set APP_SEED_RENTAL_VENDOR_PASSWORD to create the missing demo vendor accounts.");
            return;
        }
        List<UserAccount> owners = new ArrayList<>();
        for (VendorSeed seed : VENDORS) owners.add(getOrCreateVendor(seed));
        List<MarketplaceModuleCategory> active = categories.findByTypeAndActiveTrueOrderByNameAsc("RENTAL");
        if (active.size() < 5) { log.warn("Rental sample seeding expected 5 active top-level RENTAL categories but found {}.", active.size()); return; }
        int created = 0;
        LocalDate licenceExpiry = LocalDate.now().plusYears(3);
        LocalDate insuranceExpiry = LocalDate.now().plusYears(1);
        int index = 0;
        for (MarketplaceModuleCategory category : active) {
            int categoryIndex = sampleIndex(category.getName());
            if (categoryIndex < 0) { log.warn("No demo vehicle set is defined for rental category '{}'; skipping it.", category.getName()); continue; }
            List<String> subs = category.getSubcategories();
            if (subs == null || subs.isEmpty()) { log.warn("Skipping category '{}' because it has no subcategories.", category.getName()); continue; }
            for (int item = 0; item < 5; item++) {
                String seedKey = BATCH + ":" + category.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-") + ":" + (item + 1);
                if (vehicles.existsBySeedKey(seedKey)) { index++; continue; }
                UserAccount owner = owners.get(index % owners.size());
                int subIndex = item % subs.size();
                RentalVehicle v = new RentalVehicle();
                v.setOwnerUserId(owner.getId()); v.setOwnerName(owner.getFullName());
                v.setVehicleCategory(category.getName()); v.setVehicleSubcategory(subs.get(subIndex));
                v.setMakeModel(VEHICLES[categoryIndex][item]);
                v.setRegistrationNumber(String.format("DEMO-RNT-%03d", index + 1));
                v.setSeatingCapacity(CAPACITY[categoryIndex][item]);
                v.setDescription("SAMPLE TEST LISTING — driver-operated rental. Demonstration data for admin review; verify all documents and details before approval.");
                v.setImageUrl("/images/product-placeholder.svg");
                v.setDriverName("Demo Driver " + (index + 1)); v.setDriverMobile(String.format("+91990000%04d", index + 1));
                v.setDriverLicenceNumber(String.format("DEMO-DL-%06d", index + 1));
                v.setDriverLicenceValidUntil(licenceExpiry); v.setInsuranceValidUntil(insuranceExpiry);
                v.setBaseFare(new BigDecimal(categoryIndex < 2 ? "300" : categoryIndex == 2 ? "1200" : categoryIndex == 3 ? "2500" : "1800"));
                v.setPerKmRate(new BigDecimal(categoryIndex < 2 ? "8" : categoryIndex == 2 ? "14" : categoryIndex == 3 ? "22" : "18"));
                v.setStatus("PENDING"); v.setSeededTestData(true); v.setSeedBatchId(BATCH); v.setSeedKey(seedKey);
                v.setCreatedAt(Instant.now()); v.setUpdatedAt(Instant.now()); vehicles.save(v); created++; index++;
            }
        }
        log.info("Created {} pending sample rental vehicles across active categories for {} and {}.", created, VENDORS.get(0).email(), VENDORS.get(1).email());
    }

    private UserAccount getOrCreateVendor(VendorSeed seed) {
        UserAccount existing = users.findByEmailIgnoreCase(seed.email()).orElse(null);
        if (existing != null) {
            if (existing.getRole() != Role.ROLE_VENDOR) throw new IllegalStateException(seed.email() + " already exists but is not a vendor account; no changes made.");
            return existing;
        }
        UserAccount u = new UserAccount(); u.setFullName(seed.fullName()); u.setEmail(seed.email()); u.setMobile(seed.mobile());
        u.setPassword(encoder.encode(vendorPassword)); u.setRole(Role.ROLE_VENDOR); u.setVendorStatus(VendorStatus.APPROVED);
        u.setEnabled(true); u.setTermsAccepted(true); u.setAcceptedTermsVersion("1.0"); u.setTermsAcceptedAt(Instant.now());
        u.setAddressLine("Demo address — replace before real use"); u.setCity("Bhubaneswar"); u.setState("Odisha"); u.setPinCode("751001");
        UserAccount saved = users.save(u);
        VendorProfile p = new VendorProfile(); p.setUserId(saved.getId()); p.setBusinessName(seed.businessName());
        p.setBusinessDescription("Seeded demo vendor account for rental listing and moderation testing.");
        p.setAddressLine("Demo address — replace before real use"); p.setCity("Bhubaneswar"); p.setState("Odisha"); p.setPinCode("751001"); profiles.save(p);
        return saved;
    }

    private int sampleIndex(String name) {
        if (name == null) return -1;
        return switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "two-wheelers" -> 0;
            case "three-wheelers" -> 1;
            case "cars" -> 2;
            case "passenger transport" -> 3;
            case "commercial vehicles" -> 4;
            default -> -1;
        };
    }

    private void cleanup() {
        List<RentalVehicle> seeded = vehicles.findBySeedBatchIdOrderByCreatedAtDesc(BATCH);
        for (RentalVehicle v : seeded) { v.setStatus("SUSPENDED"); v.setAdminNote("Seeded demo record disabled by APP_SEED_RENTALS_DELETE."); vehicles.save(v); }
        log.info("Disabled {} seeded rental vehicles. Vendor accounts and booking history were preserved.", seeded.size());
    }
    private record VendorSeed(String email, String fullName, String mobile, String businessName) { }
}
