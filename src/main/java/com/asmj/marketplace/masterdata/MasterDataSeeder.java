package com.asmj.marketplace.masterdata;

import java.util.Locale;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Idempotent seed catalogue for reusable marketplace dropdowns. Existing admin edits are never overwritten. */
@Component
@Order(20)
public class MasterDataSeeder implements CommandLineRunner {
    private final MasterDataOptionRepository repo;
    public MasterDataSeeder(MasterDataOptionRepository repo) { this.repo = repo; }

    @Override public void run(String... args) {
        group("APPROVAL_MODULE", new String[][]{
            {"VENDOR","Vendor profile"},{"PRODUCT","Product"},{"RENTAL","Rental vehicle"},{"SERVICE_PROVIDER","Service provider"},{"SERVICE_LISTING","Service listing"},{"EMPLOYER","Employer profile"},{"JOB","Job posting"},{"EVENT","Event"},{"COURSE","Course"},{"WHOLESALE","Wholesale / B2B"},{"DELIVERY","Delivery / logistics"},{"FREELANCER","Freelancer"},{"BUSINESS","Business directory"},{"CLASSIFIED","Classified"},{"EMERGENCY_PROVIDER","Emergency service provider"}});
        group("APPROVAL_REQUEST_REASON", labels("Missing information","Document verification","Ownership verification","Business verification","Eligibility clarification","Correction required","Other"));
        group("APPROVAL_DOCUMENT_TYPE", labels("Business registration certificate","GST certificate","Identity proof","Address proof","Vehicle registration certificate","Vehicle insurance certificate","Driving licence","Educational certificate","Professional qualification","Other supporting document"));
        group("EMPLOYER_ORGANISATION_TYPE", new String[][]{{"COMPANY","Company / Business"},{"RECRUITMENT_CONSULTANCY","Recruitment consultancy"},{"STARTUP","Startup"},{"NGO","NGO / Non-profit"},{"GOVERNMENT","Government organisation"},{"EDUCATIONAL_INSTITUTE","Educational institute"},{"OTHER","Other"}});
        group("JOB_EMPLOYMENT_TYPE", labels("Full-time","Part-time","Contract","Temporary","Internship","Freelance","Apprenticeship"));
        group("JOB_WORK_MODE", labels("On-site","Remote","Hybrid"));
        group("JOB_EXPERIENCE_LEVEL", labels("Fresher","Entry level","1–3 years","3–5 years","5–10 years","10+ years"));
        group("RENTAL_FUEL_TYPE", labels("Petrol","Diesel","CNG","Electric","Hybrid","Other"));
        group("RENTAL_TRANSMISSION", labels("Manual","Automatic","CVT","AMT","Not applicable"));
        group("RENTAL_VEHICLE_TYPE", labels("Two-wheeler","Auto rickshaw","Hatchback","Sedan","SUV","MUV","Tempo traveller","Bus","Pickup","Truck","Commercial vehicle","Other"));
        group("PROVIDER_TYPE", labels("Individual","Organisation","Business / service centre","Government / public contact"));
        group("SERVICE_PRICING_TYPE", labels("Fixed price","Starting price","Per hour","Per visit","Per session","Quotation required"));
        group("SERVICE_MODE", labels("At customer location","At provider location","Online appointment","Both"));
        group("SERVICE_AVAILABILITY", labels("Available","Busy","Unavailable"));
        group("COURSE_MODE", labels("Online","Offline / Classroom","Hybrid"));
        group("COURSE_LEVEL", labels("Beginner","Intermediate","Advanced","All levels"));
        group("COURSE_TYPE", labels("Free","Paid","Certification","Workshop","Short-term training","Long-term programme"));
        group("EVENT_TYPE", labels("Conference","Workshop","Seminar","Concert","Exhibition","Festival","Sports","Community event","Other"));
        group("EVENT_MODE", labels("In-person","Online","Hybrid"));
        group("INSURANCE_TYPE", labels("Health","Life","Motor","Travel","Home","Business","Personal accident","Other"));
        group("BUSINESS_TYPE", labels("Retail shop","Restaurant / Cafe","Hotel / Accommodation","Clinic / Hospital","Educational institute","Professional office","Manufacturer","Wholesaler / Distributor","Other"));
        group("WHOLESALE_BUSINESS_TYPE", labels("Manufacturer","Wholesaler","Distributor","Retailer","Importer","Exporter","Other"));
        group("DELIVERY_VEHICLE_TYPE", labels("Bicycle","Two-wheeler","Three-wheeler","Car","Pickup","Van","Truck","Other"));
        group("FREELANCE_SKILL_CATEGORY", labels("Web development","Mobile app development","Graphic design","UI / UX design","Content writing","Digital marketing","Video editing","Photography","Accounting","Legal services","Consulting","Data / AI","Other"));
        group("CLASSIFIED_CONDITION", labels("New","Like new","Good","Fair","For parts / Repair"));
        group("PRODUCT_CONDITION", labels("New","Refurbished","Used - Like new","Used - Good","Used - Acceptable"));
        group("PAYMENT_METHOD", labels("UPI","Cash","Cash on delivery"));
        group("REFUND_CATEGORY", labels("Merchandise","Delivery charge","Other"));
        group("ORGANISATION_QUALIFICATION", labels("10th Pass","12th Pass","ITI","Diploma","Graduate","Postgraduate","Doctorate","Professional certification","Other"));
        group("EMERGENCY_CATEGORY", new String[][]{{"AMBULANCE_MEDICAL_TRANSPORT","Ambulance & medical transport"},{"FIRE_RESCUE","Fire & rescue"},{"ROADSIDE_ASSISTANCE","Roadside emergency assistance"},{"EMERGENCY_HOME_SERVICES","Emergency home services"},{"EMERGENCY_HEALTHCARE","Hospitals & emergency healthcare"},{"POLICE_PUBLIC_SAFETY","Police & public safety"}});
        children("EMERGENCY_SUBCATEGORY", new String[][]{
            {"AMBULANCE_MEDICAL_TRANSPORT","AMBULANCE","Ambulance"},{"AMBULANCE_MEDICAL_TRANSPORT","BLS","Basic life support ambulance"},{"AMBULANCE_MEDICAL_TRANSPORT","ALS","Advanced life support ambulance"},{"AMBULANCE_MEDICAL_TRANSPORT","PATIENT_TRANSPORT","Patient transport"},{"AMBULANCE_MEDICAL_TRANSPORT","BLOOD_BANK","Blood bank"},
            {"FIRE_RESCUE","FIRE_BRIGADE","Fire brigade"},{"FIRE_RESCUE","RESCUE","Rescue services"},{"FIRE_RESCUE","DISASTER_HELP","Disaster assistance"},
            {"ROADSIDE_ASSISTANCE","TOWING","Towing"},{"ROADSIDE_ASSISTANCE","PUNCTURE","Puncture repair"},{"ROADSIDE_ASSISTANCE","BATTERY","Battery jump-start"},{"ROADSIDE_ASSISTANCE","FUEL_DELIVERY","Emergency fuel delivery"},{"ROADSIDE_ASSISTANCE","VEHICLE_RECOVERY","Vehicle recovery"},
            {"EMERGENCY_HOME_SERVICES","ELECTRICIAN","Emergency electrician"},{"EMERGENCY_HOME_SERVICES","PLUMBER","Emergency plumber"},{"EMERGENCY_HOME_SERVICES","LOCKSMITH","Locksmith"},{"EMERGENCY_HOME_SERVICES","APPLIANCE_REPAIR","Appliance repair"},
            {"EMERGENCY_HEALTHCARE","HOSPITAL","Hospital emergency department"},{"EMERGENCY_HEALTHCARE","CLINIC","24-hour clinic"},{"EMERGENCY_HEALTHCARE","PHARMACY","24-hour pharmacy"},{"EMERGENCY_HEALTHCARE","DIAGNOSTICS","Emergency diagnostics"},
            {"POLICE_PUBLIC_SAFETY","POLICE_STATION","Police station"},{"POLICE_PUBLIC_SAFETY","WOMEN_HELPLINE","Women helpline"},{"POLICE_PUBLIC_SAFETY","DISASTER_CONTROL","Disaster control room"}});
        group("RENTAL_FUEL_TYPE", labels("Petrol","Diesel","CNG","Electric","Hybrid","Other"));
        group("RENTAL_TRANSMISSION", labels("Manual","Automatic","CVT","AMT","Not applicable"));
        group("INDIA_STATE", labels("Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli and Daman and Diu","Delhi","Jammu and Kashmir","Ladakh","Lakshadweep","Puducherry"));
    }

    private String[][] labels(String... values) { String[][] rows = new String[values.length][2]; for (int i=0;i<values.length;i++) rows[i]=new String[]{code(values[i]),values[i]}; return rows; }
    private void group(String key, String[][] rows) { for (String[] row:rows) seed(key,row[0],row[1],null); }
    private void children(String key, String[][] rows) { for(String[] row:rows) seed(key,row[1],row[2],row[0]); }
    private String code(String label) { return label.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+","_").replaceAll("^_|_$",""); }
    private void seed(String group,String code,String label,String parent) {
        MasterDataOption option=repo.findByGroupKeyAndCode(group,code).orElse(null);
        if(option!=null) {
            // One-time hierarchy repair for legacy emergency subcategories; do not overwrite labels/status.
            if(parent!=null && (option.getParentCode()==null || option.getParentCode().isBlank())) { option.setParentCode(parent); repo.save(option); }
            return;
        }
        option=new MasterDataOption(); option.setGroupKey(group); option.setCode(code); option.setLabel(label); option.setParentCode(parent); option.setActive(true); option.setSortOrder(100); repo.save(option);
    }
}
