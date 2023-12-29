package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.pojo.S3ConfigDetail;
import com.hoquangnam45.pharmacy.pojo.UploadSessionConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("pharma.upload")
@Getter
@Setter
public class UploadConfig {
    private S3ConfigDetail s3;
    private List<UploadSessionConfig> sessions;
    private boolean local;
}
