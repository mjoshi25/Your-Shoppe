package com.asmj.marketplace.masterdata;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class MasterDataSeeder implements CommandLineRunner {
 private final MasterDataOptionRepository repo; private final MasterDataGroupRepository groups; public MasterDataSeeder(MasterDataOptionRepository r, MasterDataGroupRepository g){repo=r;groups=g;}
 @Override public void run(String... args){
  // Registry of dropdown groups currently referenced by application forms/controllers.
  register("APPROVAL_MODULE","Approval modules","Modules that can be submitted to the Approval Centre.");
  register("APPROVAL_REQUEST_REASON","Approval clarification reasons","Reasons Admin can select when requesting more information.");
  register("APPROVAL_DOCUMENT_TYPE","Approval document types","Documents that Admin can request during verification.");
  register("EMPLOYER_ORGANISATION_TYPE","Employer organisation types","Organisation type choices on employer registration.");
  register("JOB_EMPLOYMENT_TYPE","Job employment types","Employment type choices on job postings.");
  register("INDIA_STATE","Indian States and UTs","State and Union Territory choices used by location forms.");
  register("INDIA_CITY","Indian Cities","City choices linked to a State using Parent Code.");
  register("INDIA_PINCODE","Indian PIN Codes","PIN code choices linked to a City using Parent Code.");
  register("RENTAL_FUEL_TYPE","Rental fuel types","Fuel choices for rental vehicle listings.");
  register("RENTAL_TRANSMISSION","Rental transmission types","Transmission choices for rental vehicle listings.");
  register("EMERGENCY_CATEGORY","Emergency service categories","Top-level emergency provider/request categories.");
  register("EMERGENCY_SUBCATEGORY","Emergency service subcategories","Specific emergency services linked to a category where configured.");

  seed("APPROVAL_MODULE","VENDOR","Vendor profile");seed("APPROVAL_MODULE","PRODUCT","Product");seed("APPROVAL_MODULE","RENTAL","Rental vehicle");seed("APPROVAL_MODULE","SERVICE_PROVIDER","Service provider");seed("APPROVAL_MODULE","SERVICE_LISTING","Service listing");seed("APPROVAL_MODULE","EMPLOYER","Employer profile");seed("APPROVAL_MODULE","JOB","Job posting");seed("APPROVAL_MODULE","EVENT","Event");seed("APPROVAL_MODULE","COURSE","Course");seed("APPROVAL_MODULE","WHOLESALE","Wholesale / B2B");seed("APPROVAL_MODULE","DELIVERY","Delivery / logistics");seed("APPROVAL_MODULE","FREELANCER","Freelancer");seed("APPROVAL_MODULE","BUSINESS","Business directory");seed("APPROVAL_MODULE","CLASSIFIED","Classified");
  for(String s:new String[]{"Missing information","Document verification","Ownership verification","Business verification","Eligibility clarification","Correction required","Other"})seed("APPROVAL_REQUEST_REASON",s.toUpperCase(Locale.ROOT).replace(' ','_'),s);
  for(String s:new String[]{"Business registration certificate","GST certificate","Identity proof","Address proof","Vehicle registration certificate","Vehicle insurance certificate","Driving licence","Educational certificate","Professional qualification","Other supporting document"})seed("APPROVAL_DOCUMENT_TYPE",s.toUpperCase(Locale.ROOT).replace(' ','_'),s);
  for(String s:new String[]{"Full-time","Part-time","Contract","Temporary","Internship","Freelance"})seed("JOB_EMPLOYMENT_TYPE",s.toUpperCase(Locale.ROOT).replace('-','_'),s);
  for(String state:new String[]{"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli and Daman and Diu","Delhi","Jammu and Kashmir","Ladakh","Lakshadweep","Puducherry"}) seed("INDIA_STATE",state.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+","_"),state);
  seed("EMPLOYER_ORGANISATION_TYPE","COMPANY","Company / Business");seed("EMPLOYER_ORGANISATION_TYPE","RECRUITMENT_CONSULTANCY","Recruitment consultancy");seed("EMPLOYER_ORGANISATION_TYPE","STARTUP","Startup");seed("EMPLOYER_ORGANISATION_TYPE","OTHER","Other");
  seed("APPROVAL_MODULE","EMERGENCY_PROVIDER","Emergency service provider");
  for(String[] c:new String[][]{{"AMBULANCE_MEDICAL_TRANSPORT","Ambulance & medical transport"},{"FIRE_RESCUE","Fire & rescue"},{"ROADSIDE_ASSISTANCE","Roadside emergency assistance"},{"EMERGENCY_HOME_SERVICES","Emergency home services"},{"EMERGENCY_HEALTHCARE","Hospitals & emergency healthcare"},{"POLICE_PUBLIC_SAFETY","Police & public safety"}})seed("EMERGENCY_CATEGORY",c[0],c[1]);
  for(String[] c:new String[][]{{"AMBULANCE","Ambulance"},{"BLS","Basic life support ambulance"},{"ALS","Advanced life support ambulance"},{"PATIENT_TRANSPORT","Patient transport"},{"TOWING","Towing"},{"PUNCTURE","Puncture repair"},{"BATTERY","Battery jump-start"},{"ELECTRICIAN","Emergency electrician"},{"PLUMBER","Emergency plumber"},{"LOCKSMITH","Locksmith"},{"HOSPITAL","Hospital emergency department"},{"CLINIC","24-hour clinic"},{"PHARMACY","24-hour pharmacy"},{"FIRE_BRIGADE","Fire brigade"},{"POLICE_STATION","Police station"},{"DISASTER_HELP","Disaster assistance"}})seed("EMERGENCY_SUBCATEGORY",c[0],c[1]);
  for(String s:new String[]{"Petrol","Diesel","CNG","Electric","Hybrid","Other"})seed("RENTAL_FUEL_TYPE",s.toUpperCase(Locale.ROOT),s);
  for(String s:new String[]{"Manual","Automatic","CVT","AMT","Not applicable"})seed("RENTAL_TRANSMISSION",s.toUpperCase(Locale.ROOT).replace(' ','_'),s);
 }
 private void register(String key,String name,String description){
  if(groups.findByGroupKey(key).isPresent()) return;
  MasterDataGroup g=new MasterDataGroup(); g.setGroupKey(key); g.setDisplayName(name); g.setDescription(description); g.setActive(true); groups.save(g);
 }
 private void seed(String group,String code,String label){if(repo.findByGroupKeyAndCode(group,code).isEmpty()){MasterDataOption o=new MasterDataOption();o.setGroupKey(group);o.setCode(code);o.setLabel(label);repo.save(o);}}
}
