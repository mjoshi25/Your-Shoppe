package com.asmj.marketplace.discovery;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Adds starter category trees for extension modules without replacing admin-created data. */
@Component
@Order(3)
public class MarketplaceModuleCategorySeeder implements CommandLineRunner {
    private final MarketplaceModuleCategoryRepository repository;
    public MarketplaceModuleCategorySeeder(MarketplaceModuleCategoryRepository repository) { this.repository=repository; }
    @Override public void run(String... args) {
        seed("EVENT", "Business & Professional", "Conferences", "Networking", "Trade fairs", "Workshops", "Seminars");
        seed("EVENT", "Entertainment", "Concerts", "Festivals", "Exhibitions", "Theatre & Shows", "Comedy");
        seed("EVENT", "Community & Sports", "Community events", "Sports events", "Cultural events", "Religious events");
        seed("COURSE", "Academic", "School tuition", "College subjects", "Competitive exams", "Entrance preparation");
        seed("COURSE", "Technology", "Computer basics", "Programming", "Web development", "Mobile app development", "Data science & AI", "Cybersecurity");
        seed("COURSE", "Business & Professional", "Accounting", "Digital marketing", "Business management", "Communication skills");
        seed("COURSE", "Vocational & Creative", "ITI & trades", "Electrician training", "Tailoring", "Beauty & wellness", "Photography", "Music & arts", "Languages");
        seed("WHOLESALE", "Food & Grocery", "Rice & grains", "Pulses", "Spices", "Edible oils", "Packaged foods", "Beverages");
        seed("WHOLESALE", "Fashion & Textiles", "Clothing", "Footwear", "Fabrics", "Garments", "Accessories");
        seed("WHOLESALE", "Electronics & Home", "Mobile accessories", "Consumer electronics", "Home appliances", "Furniture", "Hardware");
        seed("DELIVERY", "Local Delivery", "Food delivery", "Grocery delivery", "Parcel delivery", "Document delivery");
        seed("DELIVERY", "Transport & Logistics", "Goods transport", "Packers & movers", "Courier", "Last-mile delivery", "Commercial freight");
        seed("FREELANCE", "Technology", "Web development", "App development", "Software testing", "IT support", "Data & AI");
        seed("FREELANCE", "Creative", "Graphic design", "UI / UX design", "Content writing", "Photography", "Video editing");
        seed("FREELANCE", "Business & Professional", "Accounting", "Legal consulting", "Business consulting", "Digital marketing", "Translation");
        seed("BUSINESS", "Food & Hospitality", "Restaurants", "Cafes", "Catering", "Hotels", "Bakeries");
        seed("BUSINESS", "Retail & Shopping", "Grocery stores", "Clothing stores", "Electronics shops", "Furniture stores", "Stationery shops");
        seed("BUSINESS", "Health & Education", "Hospitals", "Clinics", "Pharmacies", "Diagnostic centres", "Schools", "Coaching centres");
        seed("BUSINESS", "Professional & Other", "Accountants", "Lawyers", "Repair centres", "Salons", "Real estate offices");
        seed("CLASSIFIED", "Vehicles", "Cars", "Motorcycles", "Scooters", "Commercial vehicles", "Bicycles");
        seed("CLASSIFIED", "Electronics", "Mobile phones", "Computers", "TV & Audio", "Cameras", "Home appliances");
        seed("CLASSIFIED", "Home & Living", "Furniture", "Home decor", "Kitchen appliances", "Tools & equipment");
        seed("CLASSIFIED", "Property", "Residential sale", "Residential rent", "Commercial property", "Land");
    }
    private void seed(String type,String name,String... children) {
        if(repository.existsByTypeAndNameIgnoreCase(type,name)) return;
        MarketplaceModuleCategory c=new MarketplaceModuleCategory(); c.setType(type); c.setName(name); c.setSubcategories(List.of(children)); c.setActive(true); repository.save(c);
    }
}
