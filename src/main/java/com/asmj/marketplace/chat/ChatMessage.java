package com.asmj.marketplace.chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private String senderType;
    private String senderName;
    private String body;
    private Instant sentAt = Instant.now();
    private List<ChatAttachment> attachments = new ArrayList<>();
    public ChatMessage() {}
    public ChatMessage(String senderType, String senderName, String body) { this.senderType=senderType; this.senderName=senderName; this.body=body; this.sentAt=Instant.now(); }
    public String getSenderType(){return senderType;} public void setSenderType(String v){senderType=v;}
    public String getSenderName(){return senderName;} public void setSenderName(String v){senderName=v;}
    public String getBody(){return body;} public void setBody(String v){body=v;}
    public Instant getSentAt(){return sentAt;} public void setSentAt(Instant v){sentAt=v;}
    public List<ChatAttachment> getAttachments(){return attachments;} public void setAttachments(List<ChatAttachment> v){attachments=v;}
}
