package com.asmj.marketplace.discovery;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "marketplace_module_categories")
@CompoundIndex(name = "module_category_unique", def = "{'type': 1, 'name': 1}", unique = true)
public class MarketplaceModuleCategory {
    @Id private String id;
    private String type;
    private String name;
    private List<String> subcategories = new ArrayList<>();
    private List<String> inactiveSubcategories = new ArrayList<>();
    private boolean active = true;
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public List<String> getSubcategories(){if(subcategories==null)subcategories=new ArrayList<>();return subcategories;}
    public void setSubcategories(List<String> v){subcategories=v==null?new ArrayList<>():v;}
    public List<String> getInactiveSubcategories(){if(inactiveSubcategories==null)inactiveSubcategories=new ArrayList<>();return inactiveSubcategories;}
    public void setInactiveSubcategories(List<String> v){inactiveSubcategories=v==null?new ArrayList<>():v;}
    public List<String> getActiveSubcategories(){return getSubcategories().stream().filter(s->!getInactiveSubcategories().contains(s)).toList();}
    public boolean isSubcategoryActive(String name){return !getInactiveSubcategories().contains(name);}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
