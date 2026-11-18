package com.asmj.marketplace.parameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
@Component
public class MarketplaceParameterSeeder implements CommandLineRunner {
 private final MarketplaceParameterRepository repo;
 @Value("${app.products.require-approval:true}") private boolean productApprovalDefault;
 public MarketplaceParameterSeeder(MarketplaceParameterRepository repo){this.repo=repo;}
 @Override public void run(String... args){if(repo.findByKey("products.require-approval").isEmpty()){MarketplaceParameter p=new MarketplaceParameter();p.setKey("products.require-approval");p.setLabel("Require admin approval for new product listings");p.setGroupName("PRODUCTS");p.setValue(Boolean.toString(productApprovalDefault));p.setValueType("BOOLEAN");p.setDescription("When enabled, newly created or edited products remain pending until admin moderation. Default preserves existing behaviour.");p.setEnabled(true);repo.save(p);}}
}
