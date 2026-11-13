package com.asmj.marketplace.config;

import com.asmj.marketplace.model.Category;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.order.MarketplaceOrderRepository;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.product.ProductStatus;
import com.asmj.marketplace.repository.CategoryRepository;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

/** Creates/removes only explicitly marked test products for the named vendor. */
@Component
@Order(2)
public class TestProductSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(TestProductSeeder.class);
    private static final String VENDOR_EMAIL = "shop1@asmj.co.in";
    private static final String SEED_BATCH = "ASMJ_SHOPPE_SHOP1_TEST_PRODUCTS_V1";

    private final UserAccountRepository users;
    private final VendorProfileRepository vendorProfiles;
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final MarketplaceOrderRepository orders;

    @Value("${app.seed.products.create:false}") private boolean createEnabled;
    @Value("${app.seed.products.delete:false}") private boolean deleteEnabled;

    public TestProductSeeder(UserAccountRepository users, VendorProfileRepository vendorProfiles,
                             CategoryRepository categories, ProductRepository products, MarketplaceOrderRepository orders) {
        this.users = users;
        this.vendorProfiles = vendorProfiles;
        this.categories = categories;
        this.products = products;
        this.orders = orders;
    }

    @Override
    public void run(String... args) {
        if (!createEnabled && !deleteEnabled) return;

        UserAccount vendor = users.findByEmailIgnoreCase(VENDOR_EMAIL).orElse(null);
        if (vendor == null) {
            log.warn("ASMJ Shoppe test product seeding skipped: vendor account {} was not found.", VENDOR_EMAIL);
            return;
        }
        if (vendor.getRole() == null || !"ROLE_VENDOR".equals(vendor.getRole().name())) {
            log.warn("ASMJ Shoppe test product seeding skipped: {} is not a vendor account.", VENDOR_EMAIL);
            return;
        }

        // Fail-safe: when both are enabled, delete only the previous marked batch; never recreate it.
        if (deleteEnabled) {
            List<Product> seeded = products.findBySeedBatchIdAndVendorUserId(SEED_BATCH, vendor.getId());
            var orderedProductIds = orders.findAllByOrderByCreatedAtDesc().stream()
                    .filter(order -> order.getItems() != null)
                    .flatMap(order -> order.getItems().stream())
                    .map(item -> item.getProductId()).collect(Collectors.toSet());
            int removed = 0;
            int archived = 0;
            for (Product product : seeded) {
                if (orderedProductIds.contains(product.getId())) {
                    product.setStatus(ProductStatus.ARCHIVED);
                    products.save(product); // Preserve product references used by existing order history.
                    archived++;
                } else {
                    products.delete(product);
                    removed++;
                }
            }
            log.info("ASMJ Shoppe test product cleanup removed {} products and archived {} with order history for {}.",
                    removed, archived, VENDOR_EMAIL);
            if (createEnabled) log.warn("Both test product flags are true; deletion took precedence and creation was skipped.");
            return;
        }

        VendorProfile profile = vendorProfiles.findByUserId(vendor.getId()).orElse(null);
        String vendorDisplayName = profile != null && profile.getBusinessName() != null && !profile.getBusinessName().isBlank()
                ? profile.getBusinessName() : vendor.getFullName();
        Map<String, Category> categoryByName = categories.findByActiveTrueOrderByNameAsc().stream()
                .collect(Collectors.toMap(c -> c.getName().toLowerCase(Locale.ROOT), Function.identity(), (a, b) -> a));

        List<SeedProduct> samples = List.of(
                new SeedProduct("cotton-tshirt", "Everyday Cotton T-Shirt", "Fashion & Clothing", "Men's Clothing", "Soft everyday cotton T-shirt for catalogue and approval testing.", "399.00", 40),
                new SeedProduct("steel-bottle", "Stainless Steel Water Bottle", "Home & Kitchen", "Kitchenware", "Reusable stainless-steel water bottle; sample listing for moderation testing.", "299.00", 30),
                new SeedProduct("notebook-combo", "Notebook and Pen Combo", "Books & Stationery", "School Supplies", "A practical notebook and pen combo for school and office use.", "149.00", 60),
                new SeedProduct("desk-lamp", "LED Study Desk Lamp", "Electronics", "Home Appliances", "Compact LED desk lamp for study-table and workspace lighting.", "699.00", 15),
                new SeedProduct("storage-container", "Kitchen Storage Container Set", "Home & Kitchen", "Storage & Organisation", "Stackable kitchen storage containers for everyday organisation.", "499.00", 20)
        );

        int created = 0;
        for (SeedProduct sample : samples) {
            String seedKey = SEED_BATCH + ":" + sample.key();
            if (products.existsBySeedKey(seedKey)) continue;
            Category category = categoryByName.get(sample.category().toLowerCase(Locale.ROOT));
            if (category == null || !category.getActiveSubcategories().contains(sample.subcategory())) {
                log.warn("Skipping test product '{}': active category/subcategory '{}'/'{}' is unavailable.",
                        sample.name(), sample.category(), sample.subcategory());
                continue;
            }
            Product product = new Product();
            product.setVendorUserId(vendor.getId());
            product.setVendorName(vendorDisplayName);
            product.setName(sample.name());
            product.setDescription(sample.description());
            product.setCategoryId(category.getId());
            product.setCategoryName(category.getName());
            product.setSubcategoryName(sample.subcategory());
            product.setHomeDelivery(profile == null || profile.isHomeDelivery());
            product.setPickupFromShop(profile == null || profile.isPickupFromShop());
            product.setPrice(new BigDecimal(sample.price()));
            product.setStockQuantity(sample.stock());
            product.setStatus(ProductStatus.PENDING);
            product.setImageUrl("/images/product-placeholder.svg");
            product.setSeededTestProduct(true);
            product.setSeedBatchId(SEED_BATCH);
            product.setSeedKey(seedKey);
            product.setCreatedAt(Instant.now());
            products.save(product);
            created++;
        }
        log.info("ASMJ Shoppe test product seeding created {} pending products for {}.", created, VENDOR_EMAIL);
    }

    private record SeedProduct(String key, String name, String category, String subcategory,
                               String description, String price, int stock) { }
}
