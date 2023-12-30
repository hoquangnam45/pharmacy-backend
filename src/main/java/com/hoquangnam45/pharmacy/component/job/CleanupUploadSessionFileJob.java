package com.hoquangnam45.pharmacy.component.job;

import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.service.UploadSessionService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Transactional
public class CleanupUploadSessionFileJob {
    private final UploadSessionService uploadSessionService;

    public CleanupUploadSessionFileJob(UploadSessionService uploadSessionService) {
        this.uploadSessionService = uploadSessionService;
    }

    @Scheduled(fixedRate = 30L, timeUnit = TimeUnit.MINUTES)
    public void cleanupExpiredUploadSession() throws IOException {
        for (UploadSession expiredSession : uploadSessionService.getAllExpiredUploadSession()) {
            uploadSessionService.deleteSession(expiredSession);
        }
    }
}
