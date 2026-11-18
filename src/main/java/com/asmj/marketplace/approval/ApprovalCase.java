package com.asmj.marketplace.approval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "approval_cases")
public class ApprovalCase {
    @Id private String id;
    @Indexed private String ownerUserId;
    @Indexed private String ownerEmail;
    @Indexed private String module;
    @Indexed private String subjectId;
    private String subjectType;
    private String subjectTitle;
    private String status = "ACTION_REQUIRED";
    private String latestAdminMessage;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant submittedAt;
    private Instant decidedAt;
    private String decidedBy;
    private List<ApprovalEntry> history = new ArrayList<>();
    private List<ApprovalAttachment> attachments = new ArrayList<>();
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getOwnerUserId(){return ownerUserId;} public void setOwnerUserId(String v){ownerUserId=v;}
    public String getOwnerEmail(){return ownerEmail;} public void setOwnerEmail(String v){ownerEmail=v;}
    public String getModule(){return module;} public void setModule(String v){module=v;}
    public String getSubjectId(){return subjectId;} public void setSubjectId(String v){subjectId=v;}
    public String getSubjectType(){return subjectType;} public void setSubjectType(String v){subjectType=v;}
    public String getSubjectTitle(){return subjectTitle;} public void setSubjectTitle(String v){subjectTitle=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getLatestAdminMessage(){return latestAdminMessage;} public void setLatestAdminMessage(String v){latestAdminMessage=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public Instant getSubmittedAt(){return submittedAt;} public void setSubmittedAt(Instant v){submittedAt=v;}
    public Instant getDecidedAt(){return decidedAt;} public void setDecidedAt(Instant v){decidedAt=v;}
    public String getDecidedBy(){return decidedBy;} public void setDecidedBy(String v){decidedBy=v;}
    public List<ApprovalEntry> getHistory(){if(history==null)history=new ArrayList<>();return history;} public void setHistory(List<ApprovalEntry> v){history=v;}
    public List<ApprovalAttachment> getAttachments(){if(attachments==null)attachments=new ArrayList<>();return attachments;} public void setAttachments(List<ApprovalAttachment> v){attachments=v;}
}
