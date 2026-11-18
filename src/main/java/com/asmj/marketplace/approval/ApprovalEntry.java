package com.asmj.marketplace.approval;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class ApprovalEntry {
 private String actorEmail; private String actorRole; private String action; private String message; private Instant createdAt=Instant.now(); private List<ApprovalAttachment> attachments=new ArrayList<>();
 public ApprovalEntry(){} public ApprovalEntry(String email,String role,String action,String message){this.actorEmail=email;this.actorRole=role;this.action=action;this.message=message;}
 public String getActorEmail(){return actorEmail;} public void setActorEmail(String v){actorEmail=v;} public String getActorRole(){return actorRole;} public void setActorRole(String v){actorRole=v;} public String getAction(){return action;} public void setAction(String v){action=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public List<ApprovalAttachment> getAttachments(){if(attachments==null)attachments=new ArrayList<>();return attachments;} public void setAttachments(List<ApprovalAttachment> v){attachments=v;}
}
