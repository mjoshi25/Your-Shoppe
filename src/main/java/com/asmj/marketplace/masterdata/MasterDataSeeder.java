package com.asmj.marketplace.masterdata;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class MasterDataSeeder implements CommandLineRunner {
 private final MasterDataOptionRepository repo; public MasterDataSeeder(MasterDataOptionRepository r){repo=r;}
 @Override public void run(String... args){
  seed("APPROVAL_MODULE","VENDOR","Vendor profile");seed("APPROVAL_MODULE","PRODUCT","Product");seed("APPROVAL_MODULE","RENTAL","Rental vehicle");seed("APPROVAL_MODULE","SERVICE_PROVIDER","Service provider");seed("APPROVAL_MODULE","SERVICE_LISTING","Service listing");seed("APPROVAL_MODULE","EMPLOYER","Employer profile");seed("APPROVAL_MODULE","JOB","Job posting");seed("APPROVAL_MODULE","EVENT","Event");seed("APPROVAL_MODULE","COURSE","Course");seed("APPROVAL_MODULE","WHOLESALE","Wholesale / B2B");seed("APPROVAL_MODULE","DELIVERY","Delivery / logistics");seed("APPROVAL_MODULE","FREELANCER","Freelancer");seed("APPROVAL_MODULE","BUSINESS","Business directory");seed("APPROVAL_MODULE","CLASSIFIED","Classified");
  for(String s:new String[]{"Missing information","Document verification","Ownership verification","Business verification","Eligibility clarification","Correction required","Other"})seed("APPROVAL_REQUEST_REASON",s.toUpperCase(Locale.ROOT).replace(' ','_'),s);
  for(String s:new String[]{"Business registration certificate","GST certificate","Identity proof","Address proof","Vehicle registration certificate","Vehicle insurance certificate","Driving licence","Educational certificate","Professional qualification","Other supporting document"})seed("APPROVAL_DOCUMENT_TYPE",s.toUpperCase(Locale.ROOT).replace(' ','_'),s);
  for(String s:new String[]{"Full-time","Part-time","Contract","Temporary","Internship","Freelance"})seed("JOB_EMPLOYMENT_TYPE",s.toUpperCase(Locale.ROOT).replace('-','_'),s);
  // India states and union territories. Cities and PIN codes are maintained by Admin under their parent option codes.
  for(String state:new String[]{"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli and Daman and Diu","Delhi","Jammu and Kashmir","Ladakh","Lakshadweep","Puducherry"}) seed("INDIA_STATE",state.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+","_"),state);
  seed("EMPLOYER_ORGANISATION_TYPE","COMPANY","Company / Business");seed("EMPLOYER_ORGANISATION_TYPE","RECRUITMENT_CONSULTANCY","Recruitment consultancy");seed("EMPLOYER_ORGANISATION_TYPE","STARTUP","Startup");seed("EMPLOYER_ORGANISATION_TYPE","OTHER","Other");
  for(String s:new String[]{"10th Pass","12th Pass","ITI","Diploma","Undergraduate","Bachelor’s Degree","Any Graduate","Postgraduate","Master’s Degree","Doctorate / PhD","Professional Certification","Any Qualification","Other"})seed("JOB_QUALIFICATION",s.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+","_"),s);
  for(String s:new String[]{"Petrol","Diesel","CNG","Electric","Hybrid","Other"})seed("RENTAL_FUEL_TYPE",s.toUpperCase(Locale.ROOT),s);
  for(String s:new String[]{"Manual","Automatic","CVT","AMT","Not applicable"})seed("RENTAL_TRANSMISSION",s.toUpperCase(Locale.ROOT).replace(' ','_'),s);
 }
 private void seed(String group,String code,String label){if(repo.findByGroupKeyAndCode(group,code).isEmpty()){MasterDataOption o=new MasterDataOption();o.setGroupKey(group);o.setCode(code);o.setLabel(label);repo.save(o);}}
}
