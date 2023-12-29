package com.hoquangnam45.pharmacy.component.job;

import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.service.UploadSessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CleanupUploadSessionFileJob {
    private final UploadSessionService uploadSessionService;

    public CleanupUploadSessionFileJob(UploadSessionService uploadSessionService) {
        this.uploadSessionService = uploadSessionService;
    }

    // Run every 15 minutes
    @Scheduled(cron = "* */15 * * * *")
    public void cleanupExpiredUploadSession() throws IOException {
        for (UploadSession expiredSession : uploadSessionService.getAllExpiredUploadSession()) {
            uploadSessionService.deleteSession(expiredSession);
        }
    }
}
