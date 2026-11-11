package com.asmj.marketplace.controller;

import com.asmj.marketplace.model.Category;
import com.asmj.marketplace.product.Product;
import com.asmj.marketplace.product.ProductRepository;
import com.asmj.marketplace.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryRepository categories; private final ProductRepository products;
    public CategoryAdminController(CategoryRepository categories, ProductRepository products){this.categories=categories;this.products=products;}
    @GetMapping public String index(Model model){model.addAttribute("categories",categories.findAll());return "admin/categories";}
    @PostMapping public String create(@RequestParam String name, RedirectAttributes ra){
        String clean=clean(name); if(categories.findAll().stream().anyMatch(c->c.getName().equalsIgnoreCase(clean))) {ra.addFlashAttribute("errorMessage","A category with this name already exists.");return "redirect:/admin/categories";}
        Category c=new Category();c.setName(clean);c.setSubcategories(new ArrayList<>());c.setActive(true);categories.save(c);ra.addFlashAttribute("successMessage","Category created.");return "redirect:/admin/categories";
    }
    @PostMapping("/{id}/rename") public String rename(@PathVariable String id,@RequestParam String name,RedirectAttributes ra){
        Category c=categories.findById(id).orElseThrow();String clean=clean(name);String old=c.getName();
        if(categories.findAll().stream().anyMatch(x->!x.getId().equals(id)&&x.getName().equalsIgnoreCase(clean))){ra.addFlashAttribute("errorMessage","Another category already uses that name.");return "redirect:/admin/categories";}
        c.setName(clean);categories.save(c);for(Product p:products.findAll())if(id.equals(p.getCategoryId())||old.equals(p.getCategoryName())){p.setCategoryName(clean);products.save(p);}ra.addFlashAttribute("successMessage","Category renamed; linked products were updated.");return "redirect:/admin/categories";
    }
    @PostMapping("/{id}/active") public String active(@PathVariable String id,@RequestParam boolean active,RedirectAttributes ra){Category c=categories.findById(id).orElseThrow();c.setActive(active);categories.save(c);ra.addFlashAttribute("successMessage",active?"Category activated.":"Category deactivated. Existing products remain unchanged.");return "redirect:/admin/categories";}
    @PostMapping("/{id}/delete") public String delete(@PathVariable String id,RedirectAttributes ra){Category c=categories.findById(id).orElseThrow();boolean used=products.findAll().stream().anyMatch(p->id.equals(p.getCategoryId())||c.getName().equals(p.getCategoryName()));if(used){ra.addFlashAttribute("errorMessage","This category is used by existing products. Deactivate it or reassign those products before deleting.");}else{categories.delete(c);ra.addFlashAttribute("successMessage","Category deleted.");}return "redirect:/admin/categories";}
    @PostMapping("/{id}/subcategories") public String addSubcategory(@PathVariable String id,@RequestParam String name,RedirectAttributes ra){Category c=categories.findById(id).orElseThrow();String clean=clean(name);if(c.getSubcategories().stream().anyMatch(s->s.equalsIgnoreCase(clean))){ra.addFlashAttribute("errorMessage","That subcategory already exists.");return "redirect:/admin/categories";}c.getSubcategories().add(clean);categories.save(c);ra.addFlashAttribute("successMessage","Subcategory added.");return "redirect:/admin/categories";}
    @PostMapping("/{id}/subcategories/rename") public String renameSubcategory(@PathVariable String id,@RequestParam String oldName,@RequestParam String name,RedirectAttributes ra){Category c=categories.findById(id).orElseThrow();String clean=clean(name);if(c.getSubcategories().stream().anyMatch(s->!s.equals(oldName)&&s.equalsIgnoreCase(clean))){ra.addFlashAttribute("errorMessage","That subcategory already exists.");return "redirect:/admin/categories";}if(!c.getSubcategories().contains(oldName)){ra.addFlashAttribute("errorMessage","Subcategory not found.");return "redirect:/admin/categories";}List<String> list=new ArrayList<>(c.getSubcategories());list.set(list.indexOf(oldName),clean);c.setSubcategories(list);List<String> inactive=new ArrayList<>(c.getInactiveSubcategories());if(inactive.remove(oldName))inactive.add(clean);c.setInactiveSubcategories(inactive);categories.save(c);for(Product p:products.findAll())if((id.equals(p.getCategoryId())||c.getName().equals(p.getCategoryName()))&&oldName.equals(p.getSubcategoryName())){p.setSubcategoryName(clean);products.save(p);}ra.addFlashAttribute("successMessage","Subcategory renamed; linked products were updated.");return "redirect:/admin/categories";}

    @PostMapping("/{id}/subcategories/active") public String subcategoryActive(@PathVariable String id,@RequestParam String name,@RequestParam boolean active,RedirectAttributes ra){Category c=categories.findById(id).orElseThrow();if(!c.getSubcategories().contains(name)){ra.addFlashAttribute("errorMessage","Subcategory not found.");return "redirect:/admin/categories";}List<String> inactive=new ArrayList<>(c.getInactiveSubcategories());if(active)inactive.remove(name);else if(!inactive.contains(name))inactive.add(name);c.setInactiveSubcategories(inactive);categories.save(c);ra.addFlashAttribute("successMessage",active?"Subcategory activated.":"Subcategory deactivated; existing product references remain unchanged.");return "redirect:/admin/categories";}
    @PostMapping("/{id}/subcategories/delete") public String deleteSubcategory(@PathVariable String id,@RequestParam String name,RedirectAttributes ra){Category c=categories.findById(id).orElseThrow();boolean used=products.findAll().stream().anyMatch(p->(id.equals(p.getCategoryId())||c.getName().equals(p.getCategoryName()))&&name.equals(p.getSubcategoryName()));if(used){ra.addFlashAttribute("errorMessage","This subcategory is used by products. Rename it or reassign those products before deleting.");}else{c.getSubcategories().remove(name);List<String> inactive=new ArrayList<>(c.getInactiveSubcategories());inactive.remove(name);c.setInactiveSubcategories(inactive);categories.save(c);ra.addFlashAttribute("successMessage","Subcategory deleted.");}return "redirect:/admin/categories";}
    private String clean(String value){if(value==null||value.trim().length()<2||value.trim().length()>80)throw new IllegalArgumentException("Name must be between 2 and 80 characters.");return value.trim();}
}
