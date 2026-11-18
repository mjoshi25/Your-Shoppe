package com.asmj.marketplace.controller;
import com.asmj.marketplace.parameters.*;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/admin/parameters")
public class MarketplaceParameterController {
 private final MarketplaceParameterService service; private final MarketplaceParameterRepository repo;
 public MarketplaceParameterController(MarketplaceParameterService s,MarketplaceParameterRepository r){service=s;repo=r;}
 @GetMapping public String index(Model m){m.addAttribute("parameters",service.all());m.addAttribute("parameter",new MarketplaceParameter());return "admin/parameters";}
 @PostMapping public String save(@ModelAttribute MarketplaceParameter p,Principal principal,RedirectAttributes ra){try{service.save(p,principal.getName());ra.addFlashAttribute("successMessage","Parameter saved.");}catch(IllegalArgumentException ex){ra.addFlashAttribute("errorMessage",ex.getMessage());}return "redirect:/admin/parameters";}
 @PostMapping("/{id}/toggle") public String toggle(@PathVariable String id,Principal principal,RedirectAttributes ra){repo.findById(id).ifPresent(p->{p.setEnabled(!p.isEnabled());p.setUpdatedAt(java.time.Instant.now());p.setUpdatedBy(principal.getName());repo.save(p);});ra.addFlashAttribute("successMessage","Parameter status updated.");return "redirect:/admin/parameters";}
 @GetMapping("/{id}/edit") public String edit(@PathVariable String id,Model m){m.addAttribute("parameters",service.all());m.addAttribute("parameter",repo.findById(id).orElseThrow());return "admin/parameters";}
}
