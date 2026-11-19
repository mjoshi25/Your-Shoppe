package com.asmj.marketplace.masterdata;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "marketplace_master_data_groups")
public class MasterDataGroup {
    @Id private String id;
    @Indexed(unique = true) private String groupKey;
    private String displayName;
    private String description;
    // Optional parent dropdown group; blank means this is a top-level/independent group.
    private String parentGroupKey;
    private boolean active = true;
    private Instant updatedAt = Instant.now();
    private String updatedBy;
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getGroupKey(){return groupKey;} public void setGroupKey(String v){groupKey=v;}
    public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getParentGroupKey(){return parentGroupKey;} public void setParentGroupKey(String v){parentGroupKey=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;}
}
