package com.asmj.marketplace.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Category {
    @Id private String id;
    @Indexed(unique = true) private String name;
    private List<String> subcategories = new ArrayList<>();
    private List<String> inactiveSubcategories = new ArrayList<>();
    private boolean active = true;
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public List<String> getSubcategories(){if(subcategories==null)subcategories=new ArrayList<>();return subcategories;} public void setSubcategories(List<String> v){subcategories=v;}
    public List<String> getInactiveSubcategories(){if(inactiveSubcategories==null)inactiveSubcategories=new ArrayList<>();return inactiveSubcategories;} public void setInactiveSubcategories(List<String> v){inactiveSubcategories=v==null?new ArrayList<>():v;}
    public List<String> getActiveSubcategories(){return getSubcategories().stream().filter(s->!getInactiveSubcategories().contains(s)).toList();}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
