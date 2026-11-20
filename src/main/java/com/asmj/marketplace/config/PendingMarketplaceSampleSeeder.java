package com.asmj.marketplace.config;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.discovery.*;
import com.asmj.marketplace.services.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Optional, idempotent pending sample submissions for the three named vendor accounts.
 * Does not create users, alter vendor approvals, or approve/publicly expose any listing.
 */
@Component
public class PendingMarketplaceSampleSeeder implements ApplicationRunner {
    private final UserAccountRepository users;
    private final MarketplaceExtensionListingRepository extensionListings;
    private final CommunityListingRepository communityListings;
    private final ServiceListingRepository serviceListings;
    private final ServiceCategoryRepository serviceCategories;
    private final ServiceProviderRepository serviceProviders;
    private final MarketplaceModuleCategoryRepository moduleCategories;
    @Value("${app.seed.pending-requests.create:false}") private boolean enabled;

    public PendingMarketplaceSampleSeeder(UserAccountRepository users,
            MarketplaceExtensionListingRepository extensionListings,
            CommunityListingRepository communityListings,
            ServiceListingRepository serviceListings,
            ServiceCategoryRepository serviceCategories,
            ServiceProviderRepository serviceProviders,
            MarketplaceModuleCategoryRepository moduleCategories) {
        this.users=users; this.extensionListings=extensionListings;
        this.communityListings=communityListings; this.serviceListings=serviceListings;
        this.serviceCategories=serviceCategories; this.serviceProviders=serviceProviders; this.moduleCategories=moduleCategories;
    }

    @Override public void run(ApplicationArguments args) {
        if (!enabled) return;
        List<String> vendorEmails=List.of("shop1@asmj.co.in","shop2@asmj.co.in","shop3@asmj.co.in");
        for (String email:vendorEmails) {
            UserAccount vendor=users.findByEmailIgnoreCase(email).orElse(null);
            if (vendor==null) continue;
            ServiceProvider provider=serviceProviders.findByEmailIgnoreCase(email).orElseGet(()->createServiceProvider(vendor));
            seedServices(vendor, provider);
            seedExtensions(vendor);
            seedCommunity(vendor);
        }
    }

    private ServiceProvider createServiceProvider(UserAccount vendor) {
        ServiceProvider p=new ServiceProvider(); p.setEmail(vendor.getEmail());
        p.setProviderType("BUSINESS"); p.setDisplayName(vendor.getFullName());
        p.setContactName(vendor.getFullName()); p.setMobile(vendor.getMobile());
        p.setBusinessAddress("Sample business address"); p.setCity("Bhubaneswar");
        p.setState("Odisha"); p.setPinCodes("751001,751003,751007");
        p.setStatus("PENDING"); p.setExperienceYears(2);
        p.setQualifications("Sample profile for Admin review"); return serviceProviders.save(p);
    }

    private void seedServices(UserAccount vendor, ServiceProvider provider) {
        ServiceCategory beauty=serviceCategories.findByNameIgnoreCaseAndParentIdIsNullAndActiveTrue("Beauty & Personal Care").orElse(null);
        if (beauty!=null) {
            List<ServiceCategory> children=serviceCategories.findByParentIdAndActiveTrueOrderByNameAsc(beauty.getId());
            for (ServiceCategory child:children) {
                String title=vendor.getFullName()+" - "+child.getName()+" Sample";
                if (serviceListings.findByProviderIdOrderByCreatedAtDesc(provider.getId()).stream().anyMatch(x->title.equals(x.getTitle()))) continue;
                ServiceListing x=new ServiceListing();
                x.setProviderId(provider.getId()); x.setProviderName(vendor.getFullName());
                x.setCategoryId(beauty.getId()); x.setCategoryName(beauty.getName());
                x.setSubcategoryId(child.getId()); x.setSubcategoryName(child.getName());
                x.setTitle(title); x.setDescription("Sample pending approval listing for "+child.getName()+". Please review service details before approval.");
                x.setPricingType("FIXED"); x.setPrice(new BigDecimal("499.00"));
                x.setDurationMinutes(60); x.setAvailableDays("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");
                x.setStartTime("09:00"); x.setEndTime("19:00"); x.setSlotIntervalMinutes(60);
                x.setServiceMode("HOME_VISIT"); x.setServicePinCodes("751001,751003,751007");
                x.setActive(true); x.setStatus("PENDING"); x.setCreatedAt(Instant.now());
                serviceListings.save(x);
            }
        }
        // One general service sample for each vendor, if an active top-level service category exists.
        List<ServiceCategory> roots=serviceCategories.findByParentIdIsNullAndActiveTrueOrderByNameAsc();
        for (ServiceCategory root:roots) {
            if ("Beauty & Personal Care".equalsIgnoreCase(root.getName())) continue;
            for (ServiceCategory child:serviceCategories.findByParentIdAndActiveTrueOrderByNameAsc(root.getId())) {
                String title=vendor.getFullName()+" - "+child.getName()+" Sample";
                if (serviceListings.findByProviderIdOrderByCreatedAtDesc(provider.getId()).stream().anyMatch(x->title.equals(x.getTitle()))) continue;
                ServiceListing x=new ServiceListing(); x.setProviderId(provider.getId()); x.setProviderName(vendor.getFullName());
                x.setCategoryId(root.getId()); x.setCategoryName(root.getName()); x.setSubcategoryId(child.getId()); x.setSubcategoryName(child.getName());
                x.setTitle(title); x.setDescription("Sample pending service submission for "+child.getName()+".");
                x.setPricingType("FIXED"); x.setPrice(new BigDecimal("399.00")); x.setDurationMinutes(60);
                x.setAvailableDays("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday"); x.setStartTime("09:00");
                x.setEndTime("18:00"); x.setSlotIntervalMinutes(60); x.setServiceMode("HOME_VISIT");
                x.setServicePinCodes("751001,751003,751007"); x.setActive(true); x.setStatus("PENDING");
                x.setCreatedAt(Instant.now()); serviceListings.save(x);
            }
        }
        ServiceCategory general=roots.stream().filter(c->!"Beauty & Personal Care".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        if (general!=null) {
            String title=vendor.getFullName()+" - General Home Service Sample";
            if (serviceListings.findByProviderIdOrderByCreatedAtDesc(provider.getId()).stream().noneMatch(x->title.equals(x.getTitle()))) {
                ServiceListing x=new ServiceListing(); x.setProviderId(provider.getId()); x.setProviderName(vendor.getFullName());
                x.setCategoryId(general.getId()); x.setCategoryName(general.getName()); x.setTitle(title);
                x.setDescription("Sample home service listing awaiting Admin approval.");
                x.setPricingType("FIXED"); x.setPrice(new BigDecimal("350.00")); x.setDurationMinutes(60);
                x.setAvailableDays("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday"); x.setStartTime("09:00");
                x.setEndTime("18:00"); x.setSlotIntervalMinutes(60); x.setServiceMode("HOME_VISIT");
                x.setServicePinCodes("751001,751003,751007"); x.setActive(true); x.setStatus("PENDING");
                x.setCreatedAt(Instant.now()); serviceListings.save(x);
            }
        }
    }

    private void seedExtensions(UserAccount vendor) {
        Map<String,String> moduleTitles=Map.of(
            "EVENT","Community Business Networking Meet",
            "COURSE","Practical Digital Skills Training",
            "WHOLESALE","Wholesale Sample Product Supply",
            "DELIVERY","Local Parcel Delivery Service",
            "FREELANCE","Professional Website Design Service");
        for (Map.Entry<String,String> entry:moduleTitles.entrySet()) {
            String type=entry.getKey();
            List<MarketplaceModuleCategory> cats=moduleCategories.findByTypeAndActiveTrueOrderByNameAsc(type);
            if (cats.isEmpty()) continue;
            // One pending sample per configured top-level category and vendor.
            for (MarketplaceModuleCategory cat:cats) {
                String title=vendor.getFullName()+" - "+entry.getValue()+" ("+cat.getName()+")";
                boolean exists=extensionListings.findByOwnerUserIdOrderByCreatedAtDesc(vendor.getId())
                    .stream().anyMatch(x->title.equals(x.getTitle()) && type.equals(x.getType()));
                if (exists) continue;
                MarketplaceExtensionListing x=new MarketplaceExtensionListing();
                x.setType(type); x.setOwnerUserId(vendor.getId()); x.setTitle(title);
                x.setCategory(cat.getName());
                x.setSubcategory(cat.getSubcategories().isEmpty()?"":cat.getSubcategories().get(0));
                x.setDescription("Demonstration submission for "+cat.getName()+". This sample is intentionally pending Admin review.");
                x.setOrganisation(vendor.getFullName()); x.setContactName(vendor.getFullName());
                x.setContactMobile(vendor.getMobile()); x.setAddress("Sample business address");
                x.setCity("Bhubaneswar"); x.setState("Odisha"); x.setPinCode("751001");
                x.setPrice(type.equals("EVENT")?new BigDecimal("500.00"):new BigDecimal("999.00"));
                x.setCapacity(type.equals("EVENT")?100:null); x.setAvailableQuantity(type.equals("EVENT")?100:null);
                x.setOnlineBookingEnabled(false);
                if ("EVENT".equals(type)) x.setEventDate(LocalDate.now().plusMonths(2));
                if ("COURSE".equals(type)) { x.setMode("ONLINE"); x.setDuration("4 weeks"); x.setCourseLevel("Beginner"); }
                if ("DELIVERY".equals(type)) { x.setDeliveryArea("Bhubaneswar"); x.setMode("LOCAL"); }
                if ("FREELANCE".equals(type)) x.setSkills("Website design, responsive UI, maintenance");
                x.setStatus("PENDING"); x.setCreatedAt(Instant.now()); x.setUpdatedAt(Instant.now());
                extensionListings.save(x);
            }
        }
    }

    private void seedCommunity(UserAccount vendor) {
        for (String type:List.of("BUSINESS","CLASSIFIED")) {
            List<MarketplaceModuleCategory> cats=moduleCategories.findByTypeAndActiveTrueOrderByNameAsc(type);
            if (cats.isEmpty()) continue;
            for (MarketplaceModuleCategory cat:cats) {
                String title=vendor.getFullName()+" - "+("BUSINESS".equals(type)?"Sample Local Business":"Sample Classified Item")+" ("+cat.getName()+")";
                boolean exists=communityListings.findByOwnerUserIdOrderByCreatedAtDesc(vendor.getId())
                    .stream().anyMatch(x->title.equals(x.getTitle()) && type.equals(x.getType()));
                if (exists) continue;
                CommunityListing x=new CommunityListing(); x.setType(type); x.setOwnerUserId(vendor.getId());
                x.setTitle(title); x.setCategory(cat.getName());
                x.setSubcategory(cat.getSubcategories().isEmpty()?"":cat.getSubcategories().get(0));
                x.setDescription("Demonstration "+type.toLowerCase(Locale.ROOT)+" submission for "+cat.getName()+". Pending Admin approval.");
                x.setContactName(vendor.getFullName()); x.setContactEmail(vendor.getEmail()); x.setContactMobile(vendor.getMobile());
                x.setAddress("Sample business address"); x.setCity("Bhubaneswar"); x.setState("Odisha"); x.setPinCode("751001");
                if ("CLASSIFIED".equals(type)) { x.setCondition("NEW"); x.setPrice(new BigDecimal("15000.00")); }
                x.setStatus("PENDING"); x.setCreatedAt(Instant.now()); x.setUpdatedAt(Instant.now());
                communityListings.save(x);
            }
        }
    }
}
