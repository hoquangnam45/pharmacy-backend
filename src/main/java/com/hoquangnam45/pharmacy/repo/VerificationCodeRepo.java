package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.constant.VerificationType;
import com.hoquangnam45.pharmacy.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VerificationCodeRepo extends JpaRepository<VerificationCode, UUID> {
    @Modifying
    void deleteAllByExpiredAtBefore(OffsetDateTime now);
    VerificationCode findByTypeAndVerificationCode(VerificationType verificationType, String verificationCode);
    VerificationCode findByTypeAndUserId(VerificationType verificationType, UUID userId);
}
