package com.asmj.marketplace.rental;

import com.asmj.marketplace.discovery.MarketplaceModuleCategory;
import com.asmj.marketplace.discovery.MarketplaceModuleCategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class RentalCategorySeeder implements CommandLineRunner {
    private final MarketplaceModuleCategoryRepository categories;
    public RentalCategorySeeder(MarketplaceModuleCategoryRepository categories) { this.categories = categories; }
    @Override public void run(String... args) {
        if (!categories.findByTypeOrderByNameAsc("RENTAL").isEmpty()) return;
        seed("Two-wheelers", "Motorcycles", "Scooters", "Electric scooters");
        seed("Three-wheelers", "Auto rickshaws", "E-rickshaws", "Cargo three-wheelers");
        seed("Cars", "Hatchbacks", "Sedans", "SUVs", "MUVs", "Luxury cars");
        seed("Passenger transport", "Tempo Travellers", "Minibuses", "Buses");
        seed("Commercial vehicles", "Pickup vehicles", "Mini trucks", "Trucks", "Containers");
    }
    private void seed(String name, String... children) {
        MarketplaceModuleCategory c = new MarketplaceModuleCategory();
        c.setType("RENTAL"); c.setName(name); c.setSubcategories(List.of(children)); c.setActive(true);
        categories.save(c);
    }
}
