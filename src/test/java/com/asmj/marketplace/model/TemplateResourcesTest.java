package com.asmj.marketplace.model;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
class TemplateResourcesTest {
 @Test void allControllerReturnedTemplatesArePackaged(){
  String[] templates={"chat/inbox.html","chat/conversation.html","vendor/orders.html","order/cart.html","admin/categories.html","admin/commissions.html","vendor/commissions.html","vendor/product-form.html","vendor/products.html","settings/index.html"};
  for(String template:templates) assertTrue(new ClassPathResource("templates/"+template).exists(),"Missing Thymeleaf template: "+template);
 }
 @Test void registrationFormIncludesExplicitCsrfTokenForBothSubmitActions() throws Exception {
  ClassPathResource resource = new ClassPathResource("templates/auth/register.html");
  String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  assertTrue(html.contains("th:name=\"${_csrf.parameterName}\""), "Registration form must submit CSRF token");
  assertTrue(html.contains("@{/register/customer}") && html.contains("@{/register/vendor}"), "Both registration actions must remain available");
 }
}
