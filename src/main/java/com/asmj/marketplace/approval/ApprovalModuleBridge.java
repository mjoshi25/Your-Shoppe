package com.asmj.marketplace.approval;

import com.asmj.marketplace.model.Role;
import com.asmj.marketplace.model.UserAccount;
import com.asmj.marketplace.repository.UserAccountRepository;
import com.asmj.marketplace.service.VendorManagementService;
import java.time.Instant;
import java.util.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/** Links approval-support cases to the existing module records without replacing their native workflows. */
@Service
public class ApprovalModuleBridge {
 private final MongoTemplate mongo; private final UserAccountRepository users; private final VendorManagementService vendors;
 public ApprovalModuleBridge(MongoTemplate mongo,UserAccountRepository users,VendorManagementService vendors){this.mongo=mongo;this.users=users;this.vendors=vendors;}
 private record Link(String collection,String ownerField,String titleField,String module){}
 private Link link(String module){return switch(module.toUpperCase(Locale.ROOT)){
  case "VENDOR" -> new Link("users","_id","fullName","VENDOR");
  case "PRODUCT" -> new Link("products","vendorUserId","name","PRODUCT");
  case "RENTAL" -> new Link("rental_vehicles","ownerUserId","makeModel","RENTAL");
  case "SERVICE_PROVIDER" -> new Link("service_providers","email","displayName","SERVICE_PROVIDER");
  case "SERVICE_LISTING" -> new Link("service_listings","providerId","title","SERVICE_LISTING");
  case "EMERGENCY_PROVIDER" -> new Link("emergency_providers","ownerUserId","providerName","EMERGENCY_PROVIDER");
  case "EMPLOYER" -> new Link("job_employer_profiles","userId","organisationName","EMPLOYER");
  case "JOB" -> new Link("job_postings","employerUserId","title","JOB");
  case "EVENT","COURSE","WHOLESALE","DELIVERY","FREELANCER" -> new Link("marketplace_extension_listings","ownerUserId","title",module.toUpperCase(Locale.ROOT));
  case "BUSINESS","CLASSIFIED" -> new Link("community_listings","ownerUserId","title",module.toUpperCase(Locale.ROOT));
  default -> null;
 };}
 public UserAccount validateAndResolveOwner(String module,String subjectId,String requestedEmail){Link l=link(module);if(l==null)throw new IllegalArgumentException("Unsupported approval module. Choose a supported module type.");Document d=mongo.findById(subjectId,Document.class,l.collection());if(d==null)throw new IllegalArgumentException("The selected source record was not found.");Object owner=d.get(l.ownerField());String ownerId=null;
  if("VENDOR".equals(l.module())){ownerId=String.valueOf(d.get("_id"));}else if("SERVICE_PROVIDER".equals(l.module())){String email=String.valueOf(owner);ownerId=users.findByEmailIgnoreCase(email).map(UserAccount::getId).orElse(null);}else if("SERVICE_LISTING".equals(l.module())){Document provider=mongo.findById(String.valueOf(owner),Document.class,"service_providers");if(provider!=null)ownerId=users.findByEmailIgnoreCase(String.valueOf(provider.get("email"))).map(UserAccount::getId).orElse(null);}else ownerId=owner==null?null:String.valueOf(owner);
  UserAccount account=ownerId==null?null:users.findById(ownerId).orElse(null);if(account==null||account.getRole()==Role.ROLE_ADMIN)throw new IllegalArgumentException("Could not resolve the submitter account for this source record.");if(requestedEmail!=null&&!requestedEmail.isBlank()&&!account.getEmail().equalsIgnoreCase(requestedEmail.trim()))throw new IllegalArgumentException("Selected submitter does not own this source record. Expected: "+account.getEmail());return account;
 }
 public String title(String module,String id){Link l=link(module);if(l==null)return id;Document d=mongo.findById(id,Document.class,l.collection());if(d==null)throw new IllegalArgumentException("Source record not found.");Object t=d.get(l.titleField());return t==null?id:String.valueOf(t);}
 public void applyDecision(ApprovalCase c,String decision,String actor,String note){Link l=link(c.getModule());if(l==null)throw new IllegalArgumentException("This approval case is not linked to a supported module record.");if("VENDOR".equals(l.module())){if("APPROVED".equals(decision))vendors.approve(c.getSubjectId(),actor,note);else vendors.reject(c.getSubjectId(),actor,(note==null||note.isBlank()?"Rejected after approval review":note));return;}
  String target="APPROVED".equals(decision)?"APPROVED":"REJECTED";String field="status";
  if("PRODUCT".equals(l.module()))target="APPROVED".equals(decision)?"ACTIVE":"REJECTED";
  if("JOB".equals(l.module()))target="APPROVED".equals(decision)?"PUBLISHED":"REJECTED";
  Query q=Query.query(Criteria.where("_id").is(c.getSubjectId()));Update u=new Update().set(field,target);
  if(Set.of("RENTAL","SERVICE_PROVIDER","SERVICE_LISTING","EMPLOYER","JOB").contains(l.module()))u.set("adminNote",note==null?"":note);
  if("RENTAL".equals(l.module()))u.set("updatedAt",Instant.now());
  if("EMERGENCY_PROVIDER".equals(l.module())){u.set("updatedAt",Instant.now());if("APPROVED".equals(decision))u.set("approvedAt",Instant.now());}
  if("SERVICE_PROVIDER".equals(l.module())||"SERVICE_LISTING".equals(l.module()))if("APPROVED".equals(decision))u.set("approvedAt",Instant.now());
  if("EMPLOYER".equals(l.module()))u.set("reviewedAt",Instant.now());
  if("JOB".equals(l.module()))u.set("updatedAt",Instant.now());
  if(mongo.updateFirst(q,u,l.collection()).getMatchedCount()==0)throw new IllegalArgumentException("Linked source record no longer exists.");
 }
}
