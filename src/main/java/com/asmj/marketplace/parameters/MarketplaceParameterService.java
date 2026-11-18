package com.asmj.marketplace.parameters;
import java.util.*;
import org.springframework.stereotype.Service;
@Service
public class MarketplaceParameterService {
 private final MarketplaceParameterRepository repo;
 public MarketplaceParameterService(MarketplaceParameterRepository repo){this.repo=repo;}
 public String get(String key,String fallback){return repo.findByKey(key).filter(MarketplaceParameter::isEnabled).map(MarketplaceParameter::getValue).filter(v->v!=null&&!v.isBlank()).orElse(fallback);}
 public boolean getBoolean(String key,boolean fallback){String v=get(key,null);return v==null?fallback:Boolean.parseBoolean(v);}
 public List<MarketplaceParameter> all(){return repo.findAllByOrderByGroupNameAscLabelAsc();}
 public MarketplaceParameter save(MarketplaceParameter p,String actor){if(p.getKey()==null||!p.getKey().matches("[a-zA-Z0-9._-]{3,100}"))throw new IllegalArgumentException("Parameter key must be 3–100 letters, numbers, dots, underscores or hyphens.");if(p.getLabel()==null||p.getLabel().isBlank())throw new IllegalArgumentException("Enter a parameter label.");if(p.getValueType()==null||!Set.of("TEXT","BOOLEAN","NUMBER","URL").contains(p.getValueType()))throw new IllegalArgumentException("Unsupported parameter type.");if(p.getValue()==null||p.getValue().length()>2000)throw new IllegalArgumentException("Enter a value up to 2000 characters.");if("BOOLEAN".equals(p.getValueType())&&!Set.of("true","false").contains(p.getValue().toLowerCase()))throw new IllegalArgumentException("Boolean values must be true or false.");if("NUMBER".equals(p.getValueType()))try{new java.math.BigDecimal(p.getValue());}catch(Exception e){throw new IllegalArgumentException("Enter a valid number.");}p.setKey(p.getKey().trim());p.setLabel(p.getLabel().trim());p.setGroupName(p.getGroupName()==null||p.getGroupName().isBlank()?"GENERAL":p.getGroupName().trim().toUpperCase());p.setUpdatedAt(java.time.Instant.now());p.setUpdatedBy(actor);return repo.save(p);}
}
