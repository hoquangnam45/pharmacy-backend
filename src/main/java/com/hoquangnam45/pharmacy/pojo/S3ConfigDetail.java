package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ConfigDetail {
    private String accessKey;
    private String secretKey;
    private String defaultBucketName;
    private String region;
}
