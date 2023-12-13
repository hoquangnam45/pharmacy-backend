package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class UploadFile {
    private final MultipartFile multipart;
    private final String sessionId;
    private final String contentType;
    // Set the uploaded key to aws s3
    private final String uploadedFileName;
}
