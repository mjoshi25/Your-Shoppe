package com.asmj.marketplace.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryImageService {
    private final String cloudName, apiKey, apiSecret;
    public CloudinaryImageService(@Value("${cloudinary.cloud-name:}") String cloudName,
                                  @Value("${cloudinary.api-key:}") String apiKey,
                                  @Value("${cloudinary.api-secret:}") String apiSecret) {
        this.cloudName=cloudName; this.apiKey=apiKey; this.apiSecret=apiSecret;
    }

    public Map<String,Object> uploadProductImage(MultipartFile file) {
        validate(file, 5L * 1024 * 1024);
        String type=contentType(file);
        if (!type.equals("image/jpeg") && !type.equals("image/png") && !type.equals("image/webp"))
            throw new IllegalArgumentException("Only JPG, PNG or WEBP images are supported.");
        return upload(file, "asmj-marketplace/products", "image");
    }

    /** Upload a profile photo as an image (JPG, PNG or WEBP), maximum 5 MB. */
    public Map<String,Object> uploadProfilePhoto(MultipartFile file) {
        validate(file, 5L * 1024 * 1024);
        String type=contentType(file);
        if (!type.equals("image/jpeg") && !type.equals("image/png") && !type.equals("image/webp"))
            throw new IllegalArgumentException("Profile photos must be JPG, PNG or WEBP images.");
        return upload(file, "asmj-marketplace/profile-photos", "image");
    }

    /** Uploads marketplace advertising banners (JPG, PNG or WEBP), maximum 5 MB. */
    public Map<String,Object> uploadAdvertisementBanner(MultipartFile file) {
        validate(file, 5L * 1024 * 1024);
        String type=contentType(file);
        if (!type.equals("image/jpeg") && !type.equals("image/png") && !type.equals("image/webp"))
            throw new IllegalArgumentException("Advertisement banners must be JPG, PNG or WEBP images.");
        return upload(file, "asmj-marketplace/advertising/banners", "image");
    }

    /** Uploads payment evidence, chat attachments and receipts. */
    public Map<String,Object> uploadDocument(MultipartFile file, String folder) {
        validate(file, 8L * 1024 * 1024);
        String type=contentType(file);
        boolean image=type.equals("image/jpeg")||type.equals("image/png")||type.equals("image/webp");
        boolean document=type.equals("application/pdf")||type.equals("text/plain")||type.equals("application/msword")||type.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        if(!image&&!document) throw new IllegalArgumentException("Allowed attachments: JPG, PNG, WEBP, PDF, DOC or DOCX.");
        return upload(file, folder, image?"image":"raw");
    }

    private void validate(MultipartFile file,long max){
        if(file==null||file.isEmpty()) throw new IllegalArgumentException("Please select a file.");
        if(file.getSize()>max) throw new IllegalArgumentException("File is too large. Maximum allowed size is "+(max/(1024*1024))+" MB.");
        String name=file.getOriginalFilename()==null?"":file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if(name.endsWith(".jsp")||name.endsWith(".html")||name.endsWith(".htm")||name.endsWith(".js")||name.endsWith(".exe")||name.endsWith(".bat")||name.endsWith(".sh")) throw new IllegalArgumentException("This file type is not allowed.");
    }
    private String contentType(MultipartFile f){return f.getContentType()==null?"":f.getContentType().toLowerCase(Locale.ROOT);}
    @SuppressWarnings("unchecked") private Map<String,Object> upload(MultipartFile file,String folder,String resourceType){
        if(cloudName.isBlank()||apiKey.isBlank()||apiSecret.isBlank()) throw new IllegalStateException("Cloudinary is not configured. Set CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY and CLOUDINARY_API_SECRET.");
        try {
            Cloudinary cloudinary=new Cloudinary(ObjectUtils.asMap("cloud_name",cloudName,"api_key",apiKey,"api_secret",apiSecret,"secure",true));
            return (Map<String,Object>)cloudinary.uploader().upload(file.getBytes(),ObjectUtils.asMap("folder",folder,"resource_type",resourceType,"use_filename",true,"unique_filename",true));
        } catch(IOException ex){throw new IllegalStateException("Unable to upload file. Please try again.",ex);}
    }
}
