package com.asmj.marketplace.discovery;

import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommunityDirectoryController {
 private final CommunityListingRepository listings; private final UserAccountRepository users;
 public CommunityDirectoryController(CommunityListingRepository listings,UserAccountRepository users){this.listings=listings;this.users=users;}
 private UserAccount current(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow();}
 private String val(String s,int max){if(s==null)return "";s=s.trim();return s.length()>max?s.substring(0,max):s;}
 private String form(String type,Model m){m.addAttribute("type",type);m.addAttribute("listing",new CommunityListing());return "discovery/form";}
 @GetMapping("/businesses") public String businesses(@RequestParam(required=false) String q,@RequestParam(required=false) String city,Model m){List<CommunityListing> rows=listings.findByTypeAndStatusOrderByCreatedAtDesc("BUSINESS","APPROVED");filter(rows,q,city);m.addAttribute("items",rows);m.addAttribute("type","BUSINESS");m.addAttribute("q",q);m.addAttribute("city",city);return "discovery/list";}
 @GetMapping("/classifieds") public String classifieds(@RequestParam(required=false) String q,@RequestParam(required=false) String city,Model m){List<CommunityListing> rows=listings.findByTypeAndStatusOrderByCreatedAtDesc("CLASSIFIED","APPROVED");filter(rows,q,city);m.addAttribute("items",rows);m.addAttribute("type","CLASSIFIED");m.addAttribute("q",q);m.addAttribute("city",city);return "discovery/list";}
 private void filter(List<CommunityListing> rows,String q,String city){if(q!=null&&!q.isBlank())rows.removeIf(x->!(safe(x.getTitle())+" "+safe(x.getCategory())+" "+safe(x.getDescription())).toLowerCase().contains(q.trim().toLowerCase()));if(city!=null&&!city.isBlank())rows.removeIf(x->x.getCity()==null||!x.getCity().equalsIgnoreCase(city.trim()));}
 private String safe(String s){return s==null?"":s;}
 @GetMapping("/businesses/new") public String newBusiness(Model m){return form("BUSINESS",m);}
 @GetMapping("/classifieds/new") public String newClassified(Model m){return form("CLASSIFIED",m);}
 @PostMapping("/community-listings/save") public String save(@RequestParam String type,@RequestParam String title,@RequestParam String category,@RequestParam String description,@RequestParam(required=false) String contactName,@RequestParam(required=false) String contactMobile,@RequestParam(required=false) String website,@RequestParam(required=false) String imageUrl,@RequestParam(required=false) String address,@RequestParam(required=false) String city,@RequestParam(required=false) String state,@RequestParam(required=false) String pinCode,@RequestParam(required=false) String condition,@RequestParam(required=false) String price,Authentication auth,RedirectAttributes ra){
  if(!"BUSINESS".equals(type)&&!"CLASSIFIED".equals(type))return "redirect:/";
  CommunityListing x=new CommunityListing();x.setType(type);x.setOwnerUserId(current(auth).getId());x.setTitle(val(title,120));x.setCategory(val(category,80));x.setDescription(val(description,3000));x.setContactName(val(contactName,100));x.setContactMobile(val(contactMobile,20));x.setWebsite(val(website,250));x.setImageUrl(val(imageUrl,1000));x.setAddress(val(address,250));x.setCity(val(city,80));x.setState(val(state,80));x.setPinCode(val(pinCode,10));x.setCondition(val(condition,40));if(price!=null&&!price.isBlank())try{x.setPrice(new BigDecimal(price));}catch(NumberFormatException ignored){}
  if(x.getTitle().isBlank()||x.getCategory().isBlank()||x.getDescription().isBlank()){ra.addFlashAttribute("error","Title, category and description are required.");return "redirect:/"+(type.equals("BUSINESS")?"businesses/new":"classifieds/new");}
  x.setStatus("PENDING");x.setCreatedAt(Instant.now());x.setUpdatedAt(Instant.now());listings.save(x);ra.addFlashAttribute("success","Your listing was submitted for admin review.");return "redirect:/my-community-listings";
 }
 @GetMapping("/my-community-listings") public String mine(Authentication auth,Model m){m.addAttribute("items",listings.findByOwnerUserIdOrderByCreatedAtDesc(current(auth).getId()));return "discovery/mine";}
 @GetMapping("/admin/community-listings") public String admin(Model m){m.addAttribute("items",listings.findAll());return "discovery/admin";}
 @PostMapping("/admin/community-listings/{id}/status") public String status(@PathVariable String id,@RequestParam String status,RedirectAttributes ra){if(!List.of("APPROVED","REJECTED","SUSPENDED").contains(status))return "redirect:/admin/community-listings";listings.findById(id).ifPresent(x->{x.setStatus(status);x.setUpdatedAt(Instant.now());listings.save(x);});ra.addFlashAttribute("success","Listing status updated.");return "redirect:/admin/community-listings";}
}
