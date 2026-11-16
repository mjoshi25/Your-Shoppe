package com.asmj.marketplace.services;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
@Component
public class ServiceCategorySeeder implements ApplicationRunner {
 private final ServiceCategoryRepository repository;
 public ServiceCategorySeeder(ServiceCategoryRepository repository){this.repository=repository;}
 @Override public void run(ApplicationArguments args){if(repository.count()>0)return;
  seedWithChildren("Home Maintenance","Repairs and maintenance",new String[]{"Electrician","Plumber","Carpenter","AC Repair","Appliance Repair","Painting"});
  seedWithChildren("Healthcare","Verified healthcare professionals and appointments",new String[]{"General Physician","Dentist","Dermatologist","Physiotherapist","Diagnostics"});
  seedWithChildren("Beauty & Personal Care","Salon and grooming services",new String[]{"Home Salon","Haircut & Styling","Makeup Artist","Bridal Services","Spa & Grooming"});
  seedWithChildren("Education & Training","Tutoring and training",new String[]{"Home Tutor","Computer Training","Language Training","Music Classes","Coaching"});
  seedWithChildren("Events & Photography","Event and creative services",new String[]{"Photography","Videography","Event Management","Catering","Decoration"});
  seedWithChildren("Cleaning & Pest Control","Home and business cleaning",new String[]{"Home Cleaning","Deep Cleaning","Pest Control","Office Cleaning"});
  seedWithChildren("Fitness & Wellness","Fitness and wellness professionals",new String[]{"Fitness Trainer","Yoga Instructor","Wellness Coaching"});
  seedWithChildren("Professional Services","Other professional services",new String[]{"Consulting","Computer Repair","Other Services"});
 }
 private void seedWithChildren(String name,String description,String[] children){ServiceCategory parent=seed(name,description,null);for(String child:children)seed(child,"Service under "+name,parent.getId());}
 private ServiceCategory seed(String name,String description,String parentId){ServiceCategory c=new ServiceCategory();c.setName(name);c.setDescription(description);c.setParentId(parentId);c.setActive(true);return repository.save(c);}
}
