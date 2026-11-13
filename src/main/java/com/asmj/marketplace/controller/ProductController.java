package com.asmj.marketplace.controller;

import com.asmj.marketplace.audit.AuditService;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.model.Category;
import com.asmj.marketplace.model.VendorProfile;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.product.ProductStatus;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.repository.CategoryRepository;
import com.asmj.marketplace.repository.VendorProfileRepository;
import com.asmj.marketplace.service.CloudinaryImageService;
import com.asmj.marketplace.order.MarketplaceOrderRepository;
import com.asmj.marketplace.order.MarketplaceOrder;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {
    private final ProductRepository products; private final UserAccountRepository users; private final CloudinaryImageService images;
    private final CategoryRepository categories; private final VendorProfileRepository vendorProfiles; private final MarketplaceOrderRepository orders; private final AuditService audit;
    @Value("${app.products.require-approval:true}") private boolean requireApproval;
    public ProductController(ProductRepository products, UserAccountRepository users, CloudinaryImageService images,
                             CategoryRepository categories, VendorProfileRepository vendorProfiles, MarketplaceOrderRepository orders, AuditService audit){
        this.products=products;this.users=users;this.images=images;this.categories=categories;this.vendorProfiles=vendorProfiles;this.orders=orders;this.audit=audit;
    }

    @GetMapping("/products") public String publicProducts(@RequestParam(required=false) String categoryId,@RequestParam(required=false) String subcategoryName,@RequestParam(required=false) String q,Model model){
        java.util.List<Product> visible=products.findByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE);
        if(categoryId!=null&&!categoryId.isBlank())visible=visible.stream().filter(p->categoryId.equals(p.getCategoryId())).toList();
        if(subcategoryName!=null&&!subcategoryName.isBlank())visible=visible.stream().filter(p->subcategoryName.equals(p.getSubcategoryName())).toList();
        if(q!=null&&!q.isBlank()){String term=q.trim().toLowerCase();visible=visible.stream().filter(p->(p.getName()!=null&&p.getName().toLowerCase().contains(term))||(p.getDescription()!=null&&p.getDescription().toLowerCase().contains(term))).toList();}
        model.addAttribute("products",visible);model.addAttribute("categories",categories.findByActiveTrueOrderByNameAsc());model.addAttribute("selectedCategory",categoryId==null?"":categoryId);model.addAttribute("selectedSubcategory",subcategoryName==null?"":subcategoryName);model.addAttribute("searchTerm",q==null?"":q);return "product/catalog";
    }
    @GetMapping("/vendor/products") public String vendorProducts(Principal principal,Model model){UserAccount u=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();model.addAttribute("products",products.findByVendorUserIdOrderByCreatedAtDesc(u.getId()));return "vendor/products";}
    @GetMapping("/vendor/products/new") public String newProduct(Model model, Principal principal){
        model.addAttribute("product",new Product()); model.addAttribute("categories",categories.findByActiveTrueOrderByNameAsc());
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        model.addAttribute("vendorProfile",vendorProfiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new));
        return "vendor/product-form";
    }
    @GetMapping("/api/categories/{id}/subcategories")
    @ResponseBody public java.util.List<String> subcategories(@PathVariable String id) {
        return categories.findById(id).filter(Category::isActive).map(Category::getActiveSubcategories).orElse(java.util.List.of());
    }
    @GetMapping("/vendor/settings/fulfilment") public String fulfilmentSettings(Principal principal, Model model) {
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        model.addAttribute("vendorProfile",vendorProfiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new));
        return "vendor/fulfilment";
    }
    @PostMapping("/vendor/settings/fulfilment") public String saveFulfilment(@RequestParam(defaultValue="false") boolean homeDelivery,
            @RequestParam(defaultValue="false") boolean pickupFromShop, @RequestParam(defaultValue="0") BigDecimal deliveryCharge, @RequestParam(defaultValue="false") boolean distanceBasedDeliveryEnabled, @RequestParam(defaultValue="5.0") BigDecimal freeDeliveryDistanceKm, @RequestParam(defaultValue="10.0") BigDecimal extraDeliveryRatePerKm, @RequestParam(required=false) String upiId, @RequestParam(required=false) String serviceablePinCodes, @RequestParam(required=false) String deliverySlots, @RequestParam(required=false) String pickupSlots, Principal principal, RedirectAttributes redirect) {
        if(!homeDelivery && !pickupFromShop){redirect.addFlashAttribute("errorMessage","Enable at least one fulfilment option.");return "redirect:/vendor/settings/fulfilment";}
        if(deliveryCharge==null || deliveryCharge.compareTo(BigDecimal.ZERO)<0 || deliveryCharge.compareTo(new BigDecimal("100000"))>0){redirect.addFlashAttribute("errorMessage","Enter a valid delivery charge between ₹0 and ₹100000.");return "redirect:/vendor/settings/fulfilment";}
        if(freeDeliveryDistanceKm==null || freeDeliveryDistanceKm.signum()<0 || freeDeliveryDistanceKm.compareTo(new BigDecimal("500"))>0 || extraDeliveryRatePerKm==null || extraDeliveryRatePerKm.signum()<0 || extraDeliveryRatePerKm.compareTo(new BigDecimal("10000"))>0){redirect.addFlashAttribute("errorMessage","Enter a valid free distance (0–500 km) and extra per-km rate (₹0–₹10000).");return "redirect:/vendor/settings/fulfilment";}
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
        VendorProfile profile=vendorProfiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new);
        profile.setUserId(vendor.getId()); profile.setHomeDelivery(homeDelivery); profile.setPickupFromShop(pickupFromShop); profile.setDeliveryCharge(deliveryCharge); profile.setDistanceBasedDeliveryEnabled(homeDelivery && distanceBasedDeliveryEnabled); profile.setFreeDeliveryDistanceKm(freeDeliveryDistanceKm); profile.setExtraDeliveryRatePerKm(extraDeliveryRatePerKm); profile.setUpiId(upiId==null?null:upiId.trim());
        try {
            profile.setServiceablePinCodes(parsePins(serviceablePinCodes));
            if(homeDelivery && profile.getServiceablePinCodes().isEmpty()) throw new IllegalArgumentException("Add at least one valid 6-digit serviceable PIN code before enabling delivery.");
            if(homeDelivery && distanceBasedDeliveryEnabled && (blank(profile.getAddressLine()) || blank(profile.getCity()) || blank(profile.getState()) || blank(profile.getPinCode()))) throw new IllegalArgumentException("Complete your shop address, city, state and PIN code in Profile Settings before enabling distance-based pricing.");
            profile.setDeliverySlotDefinitions(parseSlots(deliverySlots)); profile.setPickupSlotDefinitions(parseSlots(pickupSlots));
            if(homeDelivery && profile.getDeliverySlotDefinitions().isEmpty()) throw new IllegalArgumentException("Configure at least one delivery time slot.");
            if(pickupFromShop && profile.getPickupSlotDefinitions().isEmpty()) throw new IllegalArgumentException("Configure at least one pickup time slot.");
        } catch(IllegalArgumentException ex) { redirect.addFlashAttribute("errorMessage",ex.getMessage()); return "redirect:/vendor/settings/fulfilment"; }
        profile.setUpdatedAt(java.time.Instant.now()); vendorProfiles.save(profile); audit.record(vendor.getEmail(),"VENDOR","FULFILMENT_SETTINGS_UPDATED","VENDOR_PROFILE",profile.getId(),"Delivery PIN coverage and fulfilment slots updated.");
        for(Product product : products.findByVendorUserIdOrderByCreatedAtDesc(vendor.getId())) {
            product.setHomeDelivery(homeDelivery); product.setPickupFromShop(pickupFromShop); products.save(product);
        }
        redirect.addFlashAttribute("successMessage","Your customer fulfilment options have been updated.");
        return "redirect:/vendor/settings/fulfilment";
    }
    private boolean blank(String v){return v==null||v.isBlank();}
    private java.util.List<String> parsePins(String raw){
        java.util.LinkedHashSet<String> pins=new java.util.LinkedHashSet<>(); if(raw!=null) for(String p:raw.split("[,\\s]+")){String x=p.trim();if(!x.isEmpty()){if(!x.matches("[0-9]{6}"))throw new IllegalArgumentException("Every serviceable PIN code must contain exactly 6 digits.");pins.add(x);}}
        if(pins.size()>1000)throw new IllegalArgumentException("You can configure up to 1000 serviceable PIN codes."); return new java.util.ArrayList<>(pins);
    }
    private java.util.List<String> parseSlots(String raw){
        java.util.List<String> slots=new java.util.ArrayList<>(); if(raw==null)return slots;
        for(String line:raw.split("\\r?\\n")){String x=line.trim();if(x.isEmpty())continue;String[] p=x.split("\\|");if(p.length!=4)throw new IllegalArgumentException("Slot format must be DAY|HH:mm|HH:mm|capacity, one slot per line.");try{java.time.DayOfWeek.valueOf(p[0].trim().toUpperCase(java.util.Locale.ROOT));java.time.LocalTime start=java.time.LocalTime.parse(p[1].trim()),end=java.time.LocalTime.parse(p[2].trim());int cap=Integer.parseInt(p[3].trim());if(!end.isAfter(start)||cap<1||cap>500)throw new IllegalArgumentException();slots.add(p[0].trim().toUpperCase(java.util.Locale.ROOT)+"|"+start+"|"+end+"|"+cap);}catch(RuntimeException ex){throw new IllegalArgumentException("Invalid slot: "+x+". Use e.g. MONDAY|09:00|11:00|5.");}}
        if(slots.size()>100)throw new IllegalArgumentException("You can configure up to 100 time slots per fulfilment method.");return slots;
    }
    @PostMapping("/vendor/products") public String create(@RequestParam String name,@RequestParam String description,@RequestParam BigDecimal price,@RequestParam Integer stockQuantity,@RequestParam String categoryId,@RequestParam String subcategoryName,
            @RequestParam("image") MultipartFile image,Principal principal,Model model,RedirectAttributes redirect){
        try {
            if(name==null||name.trim().length()<2||name.length()>120) throw new IllegalArgumentException("Product name must be between 2 and 120 characters.");
            if(price==null||price.compareTo(BigDecimal.ZERO)<=0) throw new IllegalArgumentException("Enter a valid price greater than zero.");
            if(stockQuantity==null||stockQuantity<0) throw new IllegalArgumentException("Stock quantity cannot be negative.");
            UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
            Category category=categories.findById(categoryId).filter(Category::isActive)
                    .orElseThrow(()->new IllegalArgumentException("Please select a valid category."));
            if(subcategoryName==null || !category.getActiveSubcategories().contains(subcategoryName))
                throw new IllegalArgumentException("Please select a valid subcategory for the chosen category.");
            VendorProfile profile=vendorProfiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new);
            if(!profile.isHomeDelivery() && !profile.isPickupFromShop())
                throw new IllegalArgumentException("Please configure at least one fulfilment option in Vendor Settings before adding products.");
            Map<String,Object> upload=images.uploadProductImage(image);
            Product p=new Product();p.setVendorUserId(vendor.getId());p.setVendorName(vendor.getFullName());p.setName(name.trim());p.setDescription(description==null?"":description.trim());p.setCategoryId(category.getId());p.setCategoryName(category.getName());p.setSubcategoryName(subcategoryName);
            p.setHomeDelivery(profile.isHomeDelivery());p.setPickupFromShop(profile.isPickupFromShop());p.setPrice(price);p.setStockQuantity(stockQuantity);p.setImageUrl(String.valueOf(upload.get("secure_url")));p.setImagePublicId(String.valueOf(upload.get("public_id")));p.setStatus(requireApproval?ProductStatus.PENDING:ProductStatus.ACTIVE);products.save(p); audit.record(vendor.getEmail(),"VENDOR","PRODUCT_CREATED","PRODUCT",p.getId(),p.getName());
            redirect.addFlashAttribute("successMessage",requireApproval?"Product submitted for admin approval.":"Product published successfully.");return "redirect:/vendor/products";
         } catch(IllegalArgumentException|IllegalStateException ex){
            model.addAttribute("errorMessage",ex.getMessage());model.addAttribute("product",new Product());
            model.addAttribute("categories",categories.findByActiveTrueOrderByNameAsc());
            UserAccount current=users.findByEmailIgnoreCase(principal.getName()).orElseThrow();
            model.addAttribute("vendorProfile",vendorProfiles.findByUserId(current.getId()).orElseGet(VendorProfile::new));
            return "vendor/product-form";
        }
    }

    @GetMapping("/vendor/products/{id}/edit") public String editForm(@PathVariable String id, Principal principal, Model model) {
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow(); Product product=products.findById(id).orElseThrow();
        if(!vendor.getId().equals(product.getVendorUserId())) return "auth/access-denied";
        model.addAttribute("product",product); model.addAttribute("categories",categories.findByActiveTrueOrderByNameAsc());
        model.addAttribute("vendorProfile",vendorProfiles.findByUserId(vendor.getId()).orElseGet(VendorProfile::new)); return "vendor/product-form";
    }
    @PostMapping("/vendor/products/{id}/edit") public String updateProduct(@PathVariable String id,@RequestParam String name,@RequestParam(required=false) String description,@RequestParam BigDecimal price,@RequestParam Integer stockQuantity,@RequestParam String categoryId,@RequestParam String subcategoryName,@RequestParam(value="image",required=false) MultipartFile image,Principal principal,RedirectAttributes redirect) {
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow(); Product p=products.findById(id).orElseThrow(); if(!vendor.getId().equals(p.getVendorUserId())) return "auth/access-denied";
        try {
            if(name==null||name.trim().length()<2||name.trim().length()>120) throw new IllegalArgumentException("Product name must be between 2 and 120 characters.");
            if(price==null||price.signum()<=0) throw new IllegalArgumentException("Enter a valid price greater than zero."); if(stockQuantity==null||stockQuantity<0) throw new IllegalArgumentException("Stock quantity cannot be negative.");
            Category category=categories.findById(categoryId).filter(Category::isActive).orElseThrow(()->new IllegalArgumentException("Please select an active category."));
            if(subcategoryName==null||!category.getActiveSubcategories().contains(subcategoryName)) throw new IllegalArgumentException("Choose an active subcategory belonging to the selected category.");
            p.setName(name.trim());p.setDescription(description==null?"":description.trim());p.setPrice(price);p.setStockQuantity(stockQuantity);p.setCategoryId(category.getId());p.setCategoryName(category.getName());p.setSubcategoryName(subcategoryName);
            if(image!=null&&!image.isEmpty()){Map<String,Object> upload=images.uploadProductImage(image);p.setImageUrl(String.valueOf(upload.get("secure_url")));p.setImagePublicId(String.valueOf(upload.get("public_id")));}
            if(requireApproval)p.setStatus(ProductStatus.PENDING);else if(p.getStatus()!=ProductStatus.ARCHIVED)p.setStatus(ProductStatus.ACTIVE); products.save(p); redirect.addFlashAttribute("successMessage",p.getStatus()==ProductStatus.PENDING?"Product updated and submitted for admin review.":"Product updated.");
        } catch(IllegalArgumentException|IllegalStateException ex){redirect.addFlashAttribute("errorMessage",ex.getMessage());return "redirect:/vendor/products/"+id+"/edit";} return "redirect:/vendor/products";
    }
    @PostMapping("/vendor/products/{id}/delete") public String deleteProduct(@PathVariable String id,Principal principal,RedirectAttributes redirect) {
        UserAccount vendor=users.findByEmailIgnoreCase(principal.getName()).orElseThrow(); Product p=products.findById(id).orElseThrow(); if(!vendor.getId().equals(p.getVendorUserId())) return "auth/access-denied";
        boolean ordered=orders.findAll().stream().filter(o->o.getItems()!=null).flatMap(o->o.getItems().stream()).anyMatch(item->id.equals(item.getProductId()));
        if(ordered){p.setStatus(ProductStatus.ARCHIVED);products.save(p);redirect.addFlashAttribute("successMessage","Product has order history, so it was archived instead of permanently deleted.");}else{products.delete(p);redirect.addFlashAttribute("successMessage","Product deleted.");} return "redirect:/vendor/products";
    }

    @GetMapping("/admin/products") public String adminProducts(Model model){model.addAttribute("products",products.findAllByOrderByCreatedAtDesc());return "admin/products";}
    @PostMapping("/admin/products/{id}/approve") public String approve(@PathVariable String id,Principal principal,RedirectAttributes redirect){Product p=products.findById(id).orElseThrow();p.setStatus(ProductStatus.ACTIVE);products.save(p);audit.record(principal.getName(),"ADMIN","PRODUCT_APPROVED","PRODUCT",p.getId(),p.getName());redirect.addFlashAttribute("successMessage","Product approved and published.");return "redirect:/admin/products";}
    @PostMapping("/admin/products/{id}/reject") public String reject(@PathVariable String id,Principal principal,RedirectAttributes redirect){Product p=products.findById(id).orElseThrow();p.setStatus(ProductStatus.REJECTED);products.save(p);audit.record(principal.getName(),"ADMIN","PRODUCT_REJECTED","PRODUCT",p.getId(),p.getName());redirect.addFlashAttribute("successMessage","Product rejected.");return "redirect:/admin/products";}
}
