package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.constant.UploadHandlerType;
import com.hoquangnam45.pharmacy.pojo.UploadConfigDetailProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("pharma.upload")
@Getter
@Setter
public class UploadConfig {
    private UploadHandlerType type;
    private UploadConfigDetailProperty detail;
}
