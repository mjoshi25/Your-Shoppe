package com.asmj.marketplace.masterdata;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="marketplace_master_data")
@CompoundIndex(name="group_code_unique", def="{'groupKey':1,'code':1}", unique=true)
public class MasterDataOption {
 @Id private String id; private String groupKey; private String code; private String label; private String parentCode; private boolean active=true; private int sortOrder=100; private Instant updatedAt=Instant.now(); private String updatedBy;
 public String getId(){return id;} public void setId(String v){id=v;} public String getGroupKey(){return groupKey;} public void setGroupKey(String v){groupKey=v;} public String getCode(){return code;} public void setCode(String v){code=v;} public String getLabel(){return label;} public void setLabel(String v){label=v;} public String getParentCode(){return parentCode;} public void setParentCode(String v){parentCode=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public int getSortOrder(){return sortOrder;} public void setSortOrder(int v){sortOrder=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;} public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;}
}
