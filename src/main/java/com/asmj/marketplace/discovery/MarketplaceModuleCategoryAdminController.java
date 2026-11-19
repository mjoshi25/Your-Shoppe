package com.asmj.marketplace.discovery;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/marketplace-categories")
public class MarketplaceModuleCategoryAdminController {
    private static final Map<String,String> TYPES=Map.of("EVENT","Events","COURSE","Courses","WHOLESALE","Wholesale","DELIVERY","Delivery","FREELANCE","Freelance","BUSINESS","Business Directory","CLASSIFIED","Classifieds","RENTAL","Vehicle Rentals");
    private final MarketplaceModuleCategoryRepository categories;
    public MarketplaceModuleCategoryAdminController(MarketplaceModuleCategoryRepository categories){this.categories=categories;}
    @GetMapping public String index(@RequestParam(required=false) String type, Model model){
        String selected=TYPES.containsKey(type)?type:"WHOLESALE";
        model.addAttribute("types",TYPES); model.addAttribute("selectedType",selected);
        model.addAttribute("categories",categories.findByTypeOrderByNameAsc(selected)); return "extensions/categories-admin";
    }
    @PostMapping public String create(@RequestParam String type,@RequestParam String name,RedirectAttributes ra){
        String n=clean(name); if(!TYPES.containsKey(type)){ra.addFlashAttribute("error","Invalid module.");return "redirect:/admin/marketplace-categories";}
        if(categories.existsByTypeAndNameIgnoreCase(type,n)){ra.addFlashAttribute("error","This category already exists for the selected module.");return "redirect:/admin/marketplace-categories?type="+type;}
        MarketplaceModuleCategory c=new MarketplaceModuleCategory();c.setType(type);c.setName(n);c.setActive(true);categories.save(c);ra.addFlashAttribute("success","Category created.");return "redirect:/admin/marketplace-categories?type="+type;
    }
    @PostMapping("/{id}/subcategory") public String addSubcategory(@PathVariable String id,@RequestParam String name,RedirectAttributes ra){
        MarketplaceModuleCategory c=categories.findById(id).orElseThrow();String n=clean(name);
        if(c.getSubcategories().stream().anyMatch(s->s.equalsIgnoreCase(n)))ra.addFlashAttribute("error","Subcategory already exists.");
        else {c.getSubcategories().add(n);categories.save(c);ra.addFlashAttribute("success","Subcategory added.");}
        return "redirect:/admin/marketplace-categories?type="+c.getType();
    }
    @PostMapping("/{id}/subcategory/delete") public String deleteSubcategory(@PathVariable String id,@RequestParam String name,RedirectAttributes ra){MarketplaceModuleCategory c=categories.findById(id).orElseThrow();c.getSubcategories().removeIf(s->s.equals(name));categories.save(c);ra.addFlashAttribute("success","Subcategory removed.");return "redirect:/admin/marketplace-categories?type="+c.getType();}
    @PostMapping("/{id}/active") public String active(@PathVariable String id,@RequestParam boolean active,RedirectAttributes ra){MarketplaceModuleCategory c=categories.findById(id).orElseThrow();c.setActive(active);categories.save(c);return "redirect:/admin/marketplace-categories?type="+c.getType();}
    private String clean(String s){if(s==null||s.trim().length()<2||s.trim().length()>80)throw new IllegalArgumentException("Name must be between 2 and 80 characters.");return s.trim();}
}
