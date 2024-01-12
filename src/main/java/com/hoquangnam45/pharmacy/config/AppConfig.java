package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.component.mapper.AuditMapper;
import com.hoquangnam45.pharmacy.component.mapper.OrderMapper;
import com.hoquangnam45.pharmacy.service.IS3Service;
import com.hoquangnam45.pharmacy.service.impl.MockS3Service;
import com.hoquangnam45.pharmacy.service.impl.S3Service;
import org.apache.tika.Tika;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    public IS3Service s3Service(UploadConfig uploadConfig) {
        if (uploadConfig.isLocal()) {
            return new MockS3Service();
        } else {
            return new S3Service(uploadConfig);
        }
    }
}
