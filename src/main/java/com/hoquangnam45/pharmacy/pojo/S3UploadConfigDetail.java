package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3UploadConfigDetail {
    private String accessKey;
    private String secretKey;
    private String rootBucketName;
    private String region;
}
