package com.asmj.marketplace.chat;

public class ChatAttachment {
    private String fileName;
    private String fileUrl;
    private String contentType;
    public ChatAttachment() {}
    public ChatAttachment(String fileName,String fileUrl,String contentType){this.fileName=fileName;this.fileUrl=fileUrl;this.contentType=contentType;}
    public String getFileName(){return fileName;} public void setFileName(String v){fileName=v;}
    public String getFileUrl(){return fileUrl;} public void setFileUrl(String v){fileUrl=v;}
    public String getContentType(){return contentType;} public void setContentType(String v){contentType=v;}
}
