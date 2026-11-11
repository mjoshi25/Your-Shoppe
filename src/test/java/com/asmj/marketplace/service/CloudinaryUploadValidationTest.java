package com.asmj.marketplace.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CloudinaryUploadValidationTest {
    private final CloudinaryImageService uploads = new CloudinaryImageService("", "", "");

    @Test void rejectsUnsupportedAttachmentTypesBeforeCloudinaryCall() {
        MockMultipartFile file = new MockMultipartFile("attachment", "payload.exe", "application/octet-stream", new byte[]{1,2,3});
        assertThrows(IllegalArgumentException.class, () -> uploads.uploadDocument(file, "test"));
    }

    @Test void rejectsFilesLargerThanEightMegabytes() {
        byte[] content = new byte[8 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile("attachment", "proof.png", "image/png", content);
        assertThrows(IllegalArgumentException.class, () -> uploads.uploadDocument(file, "test"));
    }

    @Test void rejectsEmptyAttachments() {
        MockMultipartFile file = new MockMultipartFile("attachment", "", "", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> uploads.uploadDocument(file, "test"));
    }
}
