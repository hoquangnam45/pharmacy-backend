package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.controller.admin.MedicineAdminController;
import com.hoquangnam45.pharmacy.pojo.UploadSessionConfig;
import com.hoquangnam45.pharmacy.repo.FileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionFileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionRepo;
import com.hoquangnam45.pharmacy.service.S3Service;
import com.hoquangnam45.pharmacy.service.UploadSessionService;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class AppConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }
}
