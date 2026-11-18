package com.asmj.marketplace.parameters;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection="marketplace_parameters")
public class MarketplaceParameter {
 @Id private String id; @Indexed(unique=true) private String key; private String label; private String groupName="GENERAL"; private String value; private String valueType="TEXT"; private String description; private boolean enabled=true; private Instant updatedAt=Instant.now(); private String updatedBy;
 public String getId(){return id;} public void setId(String v){id=v;} public String getKey(){return key;} public void setKey(String v){key=v;} public String getLabel(){return label;} public void setLabel(String v){label=v;} public String getGroupName(){return groupName;} public void setGroupName(String v){groupName=v;} public String getValue(){return value;} public void setValue(String v){value=v;} public String getValueType(){return valueType;} public void setValueType(String v){valueType=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;} public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;}
}
