package com.asmj.marketplace.contact;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contact_queries")
public class ContactQuery {
    @Id private String id;
    private String name;
    private String email;
    private String mobile;
    private String subject;
    private String message;
    private String attachmentUrl;
    private String attachmentName;
    @Indexed private Instant createdAt = Instant.now();
    private String status = "NEW";
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public String getSubject(){return subject;} public void setSubject(String v){subject=v;}
    public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public String getAttachmentUrl(){return attachmentUrl;} public void setAttachmentUrl(String v){attachmentUrl=v;}
    public String getAttachmentName(){return attachmentName;} public void setAttachmentName(String v){attachmentName=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
