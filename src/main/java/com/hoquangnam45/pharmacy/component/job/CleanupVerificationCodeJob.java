package com.hoquangnam45.pharmacy.component.job;

import com.hoquangnam45.pharmacy.repo.VerificationCodeRepo;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Component
@Transactional
public class CleanupVerificationCodeJob {
    private final VerificationCodeRepo verificationCodeRepo;

    public CleanupVerificationCodeJob(VerificationCodeRepo verificationCodeRepo) {
        this.verificationCodeRepo = verificationCodeRepo;
    }

    @Scheduled(fixedRate = 30L, timeUnit = TimeUnit.MINUTES)
    public void cleanupExpiredVerificationCode() throws IOException {
        verificationCodeRepo.deleteAllByExpiredAtBefore(OffsetDateTime.now(ZoneOffset.UTC));
    }
}
