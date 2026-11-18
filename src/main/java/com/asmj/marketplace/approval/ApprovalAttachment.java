package com.asmj.marketplace.approval;
public class ApprovalAttachment {
 private String fileName; private String url; private String contentType; private String uploadedBy; private String visibility="APPROVAL_PARTICIPANTS";
 public ApprovalAttachment(){} public ApprovalAttachment(String n,String u,String t,String by){fileName=n;url=u;contentType=t;uploadedBy=by;}
 public String getFileName(){return fileName;} public void setFileName(String v){fileName=v;} public String getUrl(){return url;} public void setUrl(String v){url=v;} public String getContentType(){return contentType;} public void setContentType(String v){contentType=v;} public String getUploadedBy(){return uploadedBy;} public void setUploadedBy(String v){uploadedBy=v;} public String getVisibility(){return visibility;} public void setVisibility(String v){visibility=v;}
}
