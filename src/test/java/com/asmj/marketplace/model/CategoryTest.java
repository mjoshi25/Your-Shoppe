package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryTest {
    @Test void inactiveSubcategoriesAreNotReturnedAsActive(){
        Category category=new Category();category.setSubcategories(List.of("Phones","Laptops"));category.setInactiveSubcategories(List.of("Phones"));
        assertEquals(List.of("Laptops"),category.getActiveSubcategories());
    }
    @Test void nullInactiveListKeepsAllSubcategoriesActive(){
        Category category=new Category();category.setSubcategories(List.of("Phones"));category.setInactiveSubcategories(null);
        assertEquals(List.of("Phones"),category.getActiveSubcategories());
    }
}
