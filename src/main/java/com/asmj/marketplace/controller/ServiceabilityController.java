package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceabilityController {
    private final ProductRepository products;
    private final VendorProfileRepository profiles;

    public ServiceabilityController(ProductRepository products, VendorProfileRepository profiles) {
        this.products = products;
        this.profiles = profiles;
    }

    @GetMapping("/api/products/{productId}/serviceability")
    public Map<String, Object> check(@PathVariable String productId, @RequestParam String pinCode) {
        if (pinCode == null || !pinCode.matches("[0-9]{6}")) {
            return Map.of("serviceable", false, "message", "Enter a valid 6-digit PIN code.");
        }
        Product product = products.findById(productId).filter(p -> p.getStatus() == com.asmj.marketplace.product.ProductStatus.ACTIVE).orElse(null);
        if (product == null) return Map.of("serviceable", false, "message", "This product is not currently available.");
        VendorProfile profile = profiles.findByUserId(product.getVendorUserId()).orElse(null);
        boolean serviceable = profile != null && profile.isHomeDelivery()
                && profile.getServiceablePinCodes() != null
                && profile.getServiceablePinCodes().contains(pinCode);
        return Map.of("serviceable", serviceable,
                "message", serviceable ? "Delivery is available for this PIN code." : "This vendor does not deliver to this PIN code. Choose pickup or another vendor.");
    }
}
