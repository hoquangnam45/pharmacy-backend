package com.hoquangnam45.pharmacy.component.job;

import com.hoquangnam45.pharmacy.entity.FileMetadata;
import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import com.hoquangnam45.pharmacy.service.S3Service;
import com.hoquangnam45.pharmacy.service.UploadSessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupUploadSessionFileJob {
    private final UploadSessionService uploadSessionService;
    private final S3Service s3Service;

    public CleanupUploadSessionFileJob(UploadSessionService uploadSessionService, S3Service s3Service) {
        this.uploadSessionService = uploadSessionService;
        this.s3Service = s3Service;
    }

    // Run every 15 minutes
    @Scheduled(cron = "*/15 * * * *")
    public void cleanupExpiredUploadSession() {
        for (UploadSession expiredSession : uploadSessionService.getAllExpiredUploadSession()) {
            uploadSessionService.deleteSession(expiredSession);
        }
    }
}
