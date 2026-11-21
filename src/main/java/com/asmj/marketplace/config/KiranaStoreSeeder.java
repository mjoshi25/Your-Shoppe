package com.asmj.marketplace.config;

import com.asmj.marketplace.model.Category;
import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.model.VendorStatus;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.product.ProductStatus;
import com.asmj.marketplace.repository.CategoryRepository;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Opt-in, idempotent seed for Joshika Kirana Store.
 * Never overwrites an existing account, profile, or product.
 */
@Component
@Order(4)
public class KiranaStoreSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(KiranaStoreSeeder.class);
    private static final String EMAIL = "joshikiranastore@asmj.co.in";
    private static final String BATCH = "ASMJ_SHOPPE_JOSHIKA_KIRANA_V1";

    private final UserAccountRepository users;
    private final VendorProfileRepository profiles;
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final PasswordEncoder encoder;

    @Value("${app.seed.kirana.create:false}") private boolean createEnabled;
    @Value("${app.seed.kirana.password:}") private String initialPassword;

    public KiranaStoreSeeder(UserAccountRepository users, VendorProfileRepository profiles,
            CategoryRepository categories, ProductRepository products, PasswordEncoder encoder) {
        this.users = users;
        this.profiles = profiles;
        this.categories = categories;
        this.products = products;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (!createEnabled) return;
        if (!StringUtils.hasText(initialPassword)) {
            log.warn("Joshika Kirana seed skipped: set APP_SEED_KIRANA_PASSWORD before enabling APP_SEED_KIRANA_CREATE.");
            return;
        }

        UserAccount vendor = users.findByEmailIgnoreCase(EMAIL).orElse(null);
        if (vendor == null) {
            vendor = new UserAccount();
            vendor.setEmail(EMAIL);
            vendor.setFullName("Joshika Kirana Store");
            vendor.setPassword(encoder.encode(initialPassword));
            vendor.setRole(Role.ROLE_VENDOR);
            vendor.setVendorStatus(VendorStatus.PENDING);
            vendor.setEnabled(true);
            vendor.setMobile("");
            vendor = users.save(vendor);
            log.info("Created pending vendor account for {}.", EMAIL);
        } else {
            // Do not silently change an existing user's role, password, or approval state.
            if (vendor.getRole() != Role.ROLE_VENDOR) {
                log.error("Joshika Kirana seed stopped: {} already exists but is not a vendor account.", EMAIL);
                return;
            }
            if (vendor.getVendorStatus() != VendorStatus.PENDING) {
                log.error("Joshika Kirana seed stopped: existing vendor {} is not PENDING. No account state was changed.", EMAIL);
                return;
            }
        }

        VendorProfile profile = profiles.findByUserId(vendor.getId()).orElse(null);
        if (profile == null) {
            profile = new VendorProfile();
            profile.setUserId(vendor.getId());
            profile.setBusinessName("Joshika Kirana Store");
            profile.setBusinessDescription("Neighbourhood kirana store offering groceries, staples, snacks and daily essentials.");
            profile.setAddressLine("Please update shop address");
            profile.setCity("Please update");
            profile.setState("Please update");
            profile.setPinCode("");
            profile.setHomeDelivery(true);
            profile.setPickupFromShop(true);
            profiles.save(profile);
        }

        Category grocery = categories.findByActiveTrueOrderByNameAsc().stream()
                .filter(c -> c.getName() != null && c.getName().trim().equalsIgnoreCase("Grocery & Food"))
                .findFirst().orElse(null);
        if (grocery == null) {
            log.error("Joshika Kirana seed stopped: active 'Grocery & Food' category is missing. Vendor remains pending.");
            return;
        }

        List<SeedProduct> samples = List.of(
            new SeedProduct("rice-sona-masoori-5kg", "Sona Masoori Rice 5 kg", "Staples", "Everyday Sona Masoori rice for regular home cooking.", "425.00", 30),
            new SeedProduct("atta-5kg", "Whole Wheat Atta 5 kg", "Staples", "Whole wheat flour suitable for chapati and everyday cooking.", "245.00", 25),
            new SeedProduct("toor-dal-1kg", "Toor Dal 1 kg", "Staples", "Cleaned split pigeon peas for daily meals.", "165.00", 35),
            new SeedProduct("moong-dal-1kg", "Moong Dal 1 kg", "Staples", "Split yellow moong dal for khichdi, dal and snacks.", "145.00", 25),
            new SeedProduct("sugar-1kg", "Refined Sugar 1 kg", "Staples", "Everyday household sugar pack.", "48.00", 50),
            new SeedProduct("iodised-salt-1kg", "Iodised Salt 1 kg", "Staples", "Iodised table salt for everyday cooking.", "25.00", 60),
            new SeedProduct("sunflower-oil-1l", "Sunflower Oil 1 L", "Staples", "Refined sunflower cooking oil, one-litre pack.", "155.00", 30),
            new SeedProduct("mustard-oil-1l", "Mustard Oil 1 L", "Staples", "Mustard cooking oil for traditional everyday recipes.", "175.00", 20),
            new SeedProduct("tea-250g", "CTC Tea 250 g", "Beverages", "Strong CTC tea blend for daily tea preparation.", "115.00", 30),
            new SeedProduct("instant-coffee-100g", "Instant Coffee 100 g", "Beverages", "Instant coffee powder for quick preparation.", "185.00", 15),
            new SeedProduct("biscuits-family-pack", "Glucose Biscuits Family Pack", "Snacks", "Family-size pack of crisp glucose biscuits.", "35.00", 40),
            new SeedProduct("namkeen-200g", "Mixture Namkeen 200 g", "Snacks", "Crunchy savoury mixture snack pack.", "55.00", 25),
            new SeedProduct("noodles-4pack", "Instant Noodles 4-Pack", "Packaged Food", "Four-pack of quick-cooking instant noodles.", "60.00", 35),
            new SeedProduct("bath-soap-4pack", "Bath Soap 4-Pack", "Packaged Food", "Value pack of everyday bathing soap bars.", "120.00", 20),
            new SeedProduct("detergent-1kg", "Detergent Powder 1 kg", "Packaged Food", "Laundry detergent powder for regular household washing.", "110.00", 20)
        );

        int created = 0;
        for (SeedProduct sample : samples) {
            String seedKey = BATCH + ":" + sample.key();
            if (products.existsBySeedKey(seedKey)) continue;

            // This project stores grocery subcategories on the Grocery & Food category.
            // Only use configured active subcategories; never create or alter master data here.
            if (!grocery.getActiveSubcategories().contains(sample.subcategory())) {
                log.warn("Skipping '{}': active Grocery & Food subcategory '{}' is not configured.",
                        sample.name(), sample.subcategory());
                continue;
            }

            Product product = new Product();
            product.setVendorUserId(vendor.getId());
            product.setVendorName("Joshika Kirana Store");
            product.setName(sample.name());
            product.setDescription(sample.description());
            product.setCategoryId(grocery.getId());
            product.setCategoryName(grocery.getName());
            product.setSubcategoryName(sample.subcategory());
            product.setHomeDelivery(profile.isHomeDelivery());
            product.setPickupFromShop(profile.isPickupFromShop());
            product.setPrice(new BigDecimal(sample.price()));
            product.setStockQuantity(sample.stock());
            product.setStatus(ProductStatus.PENDING);
            product.setImageUrl("/images/product-placeholder.svg");
            product.setSeededTestProduct(true);
            product.setSeedBatchId(BATCH);
            product.setSeedKey(seedKey);
            product.setCreatedAt(Instant.now());
            products.save(product);
            created++;
        }
        log.info("Joshika Kirana seed completed: {} new PENDING products created; existing seeded products were preserved.", created);
    }

    private record SeedProduct(String key, String name, String subcategory,
            String description, String price, int stock) { }
}
