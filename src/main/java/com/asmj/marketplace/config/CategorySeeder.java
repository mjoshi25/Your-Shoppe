package com.asmj.marketplace.config;

import com.asmj.marketplace.model.Category;
import com.asmj.marketplace.repository.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySeeder implements CommandLineRunner {
    private final CategoryRepository categories;
    public CategorySeeder(CategoryRepository categories){this.categories=categories;}
    @Override public void run(String... args) {
        if (categories.count() > 0) return;
        seed("Fashion & Clothing", "Men's Clothing", "Women's Clothing", "Kids' Clothing", "Footwear", "Bags & Accessories");
        seed("Electronics", "Mobile Phones", "Computers & Laptops", "Audio", "Accessories", "Home Appliances");
        seed("Home & Kitchen", "Kitchenware", "Home Decor", "Furniture", "Storage & Organisation");
        seed("Grocery & Food", "Staples", "Snacks", "Beverages", "Packaged Food");
        seed("Beauty & Personal Care", "Skin Care", "Hair Care", "Personal Hygiene", "Cosmetics");
        seed("Books & Stationery", "Books", "School Supplies", "Office Supplies", "Art & Craft");
        seed("Sports & Fitness", "Fitness Equipment", "Sportswear", "Outdoor & Travel");
        seed("Handicrafts & Gifts", "Handmade Decor", "Artisan Products", "Gift Items");
    }
    private void seed(String name, String... children) {
        Category c=new Category(); c.setName(name); c.setSubcategories(List.of(children)); categories.save(c);
    }
}
