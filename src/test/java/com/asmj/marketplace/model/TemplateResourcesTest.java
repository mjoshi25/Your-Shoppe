package com.asmj.marketplace.model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class TemplateResourcesTest {
    @Test void allControllerReturnedTemplatesArePackaged() {
        String[] templates = {"chat/inbox.html", "chat/conversation.html", "vendor/orders.html",
                "order/cart.html", "admin/categories.html", "admin/commissions.html",
                "vendor/commissions.html", "vendor/product-form.html", "vendor/products.html",
                "settings/index.html", "admin/vendor-detail.html", "vendor/pending.html", "order/tracking.html", "order/invoice.html",
                "vendor/inventory.html", "vendor/reviews.html", "admin/reviews.html", "product/detail.html",
                "admin/reports.html", "admin/coupons.html", "admin/audit.html", "customer/wishlist.html",
                "customer/recently-viewed.html", "customer/alerts.html", "customer/offers.html", "order/checkout.html"};
        for (String template : templates) {
            assertTrue(new ClassPathResource("templates/" + template).exists(),
                    "Missing Thymeleaf template: " + template);
        }
    }

    @Test void checkoutSupportsDateSlotsAndDeferredUpiEvidence() throws Exception {
        String checkout = new String(new ClassPathResource("templates/order/checkout.html").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(checkout.contains("requestedDate") && checkout.contains("requestedSlotKey"), "Checkout must capture a requested date and slot");
        assertTrue(checkout.contains("Pay vendor after placing order"), "UPI must be paid after order placement");
    }

    @Test void registrationFormIncludesExplicitCsrfTokenForBothSubmitActions() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/auth/register.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(html.contains("th:name=\"${_csrf.parameterName}\""), "Registration form must submit CSRF token");
        assertTrue(html.contains("@{/register/customer}") && html.contains("@{/register/vendor}"),
                "Both registration actions must remain available");
    }

    @Test void vendorAdminChatFormsSupportMessagesAndAttachments() throws Exception {
        String admin = new String(new ClassPathResource("templates/admin/vendor-detail.html").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String vendor = new String(new ClassPathResource("templates/vendor/pending.html").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(admin.contains("/application-chat") && admin.contains("name=\"attachment\""),
                "Admin must be able to message and attach documents");
        assertTrue(vendor.contains("/vendor/application-chat") && vendor.contains("name=\"attachment\""),
                "Vendor must be able to reply and attach documents before approval");
    }
}
