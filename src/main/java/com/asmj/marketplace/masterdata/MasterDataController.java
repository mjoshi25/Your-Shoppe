package com.asmj.marketplace.masterdata;

import java.security.Principal;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MasterDataController {
 private final MasterDataOptionRepository repo; private final MasterDataGroupRepository groups;
 public MasterDataController(MasterDataOptionRepository r, MasterDataGroupRepository g){repo=r;groups=g;}
 @GetMapping("/admin/master-data") public String page(Model m){load(m,new MasterDataOption());return "admin/master-data";}
 @GetMapping("/admin/master-data/{id}/edit") public String edit(@PathVariable String id,Model m){load(m,repo.findById(id).orElseThrow());return "admin/master-data";}
 private void load(Model m,MasterDataOption option){List<MasterDataGroup> allGroups=groups.findAllByOrderByDisplayNameAsc();Map<String,String> names=new HashMap<>();for(MasterDataGroup g:allGroups)names.put(g.getGroupKey(),g.getDisplayName());m.addAttribute("options",repo.findAllByOrderByGroupKeyAscSortOrderAscLabelAsc());m.addAttribute("groups",allGroups);m.addAttribute("groupNames",names);m.addAttribute("option",option);}
 @PostMapping("/admin/master-data/groups") public String saveGroup(@RequestParam String displayName,@RequestParam(required=false) String groupKey,@RequestParam(required=false) String description,Principal p,RedirectAttributes ra){
  try{String name=clean(displayName);String key=clean(groupKey).toUpperCase(Locale.ROOT);if(key.isBlank())key=name.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+","_").replaceAll("^_+|_+$","");if(name.isBlank()||name.length()>100||key.isBlank()||key.length()>60||!key.matches("[A-Z][A-Z0-9_]*"))throw new IllegalArgumentException("Enter a valid dropdown name and Group Key (letters, numbers and underscores).");if(groups.findByGroupKey(key).isPresent())throw new IllegalArgumentException("That Group Key is already registered.");MasterDataGroup g=new MasterDataGroup();g.setDisplayName(name);g.setGroupKey(key);g.setDescription(clean(description));g.setUpdatedAt(Instant.now());g.setUpdatedBy(p.getName());groups.save(g);ra.addFlashAttribute("successMessage","Dropdown group registered. You can now add options to it.");}
  catch(IllegalArgumentException e){ra.addFlashAttribute("errorMessage",e.getMessage());}return "redirect:/admin/master-data";
 }
 @PostMapping("/admin/master-data") public String save(@ModelAttribute MasterDataOption option,Principal p,RedirectAttributes ra){try{String group=clean(option.getGroupKey()).toUpperCase(Locale.ROOT);String code=clean(option.getCode()).toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9_-]","_");String label=clean(option.getLabel());if(groups.findByGroupKey(group).filter(MasterDataGroup::isActive).isEmpty())throw new IllegalArgumentException("Select a registered, active dropdown group. Register a new group first if required.");if(code.isBlank()||code.length()>80||label.isBlank()||label.length()>120)throw new IllegalArgumentException("Check the option code and display label lengths.");Optional<MasterDataOption> duplicate=repo.findByGroupKeyAndCode(group,code);if(duplicate.isPresent()&&!Objects.equals(duplicate.get().getId(),option.getId()))throw new IllegalArgumentException("This code already exists in that dropdown group.");option.setGroupKey(group);option.setCode(code);option.setLabel(label);option.setUpdatedAt(Instant.now());option.setUpdatedBy(p.getName());repo.save(option);ra.addFlashAttribute("successMessage","Dropdown option saved.");}catch(IllegalArgumentException e){ra.addFlashAttribute("errorMessage",e.getMessage());}return "redirect:/admin/master-data";}
 @PostMapping("/admin/master-data/groups/{id}/toggle") public String toggleGroup(@PathVariable String id,Principal p,RedirectAttributes ra){groups.findById(id).ifPresent(g->{g.setActive(!g.isActive());g.setUpdatedAt(Instant.now());g.setUpdatedBy(p.getName());groups.save(g);});ra.addFlashAttribute("successMessage","Dropdown group status updated. Existing options and saved records are preserved.");return "redirect:/admin/master-data";}
 @PostMapping("/admin/master-data/{id}/toggle") public String toggle(@PathVariable String id,Principal p,RedirectAttributes ra){repo.findById(id).ifPresent(o->{o.setActive(!o.isActive());o.setUpdatedAt(Instant.now());o.setUpdatedBy(p.getName());repo.save(o);});ra.addFlashAttribute("successMessage","Dropdown option status updated. Existing saved values are preserved.");return "redirect:/admin/master-data";}
 @GetMapping("/api/master-data/{group}") @ResponseBody public List<Map<String,Object>> options(@PathVariable String group,@RequestParam(required=false) String parent){String key=group.toUpperCase(Locale.ROOT);if(groups.findByGroupKey(key).filter(MasterDataGroup::isActive).isEmpty())return List.of();return repo.findByGroupKeyAndActiveTrueOrderBySortOrderAscLabelAsc(key).stream().filter(o->parent==null||parent.isBlank()||Objects.equals(parent,o.getParentCode())).map(o->{Map<String,Object> m=new LinkedHashMap<>();m.put("code",o.getCode());m.put("label",o.getLabel());m.put("parentCode",o.getParentCode());return m;}).toList();}
 private String clean(String s){return s==null?"":s.trim();}
}
