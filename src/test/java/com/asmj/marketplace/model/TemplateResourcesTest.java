package com.asmj.marketplace.model;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
class TemplateResourcesTest {
 @Test void allControllerReturnedTemplatesArePackaged(){
  String[] templates={"chat/inbox.html","chat/conversation.html","vendor/orders.html","order/cart.html","admin/categories.html","admin/commissions.html","vendor/commissions.html","vendor/product-form.html","vendor/products.html","settings/index.html"};
  for(String template:templates) assertTrue(new ClassPathResource("templates/"+template).exists(),"Missing Thymeleaf template: "+template);
 }
}
