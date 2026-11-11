package com.asmj.marketplace.order;

import java.time.Instant;

public class OrderScheduleMessage {
 private String proposedDate; private String proposedTime; private String note; private String proposedBy; private String proposedByRole; private String status="PENDING"; private String respondedBy; private Instant createdAt=Instant.now(); private Instant respondedAt;
 public String getProposedDate(){return proposedDate;} public void setProposedDate(String v){proposedDate=v;}
 public String getProposedTime(){return proposedTime;} public void setProposedTime(String v){proposedTime=v;}
 public String getNote(){return note;} public void setNote(String v){note=v;}
 public String getProposedBy(){return proposedBy;} public void setProposedBy(String v){proposedBy=v;}
 public String getProposedByRole(){return proposedByRole;} public void setProposedByRole(String v){proposedByRole=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
 public String getRespondedBy(){return respondedBy;} public void setRespondedBy(String v){respondedBy=v;}
 public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
 public Instant getRespondedAt(){return respondedAt;} public void setRespondedAt(Instant v){respondedAt=v;}
}
