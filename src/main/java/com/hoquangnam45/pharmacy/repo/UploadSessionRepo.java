package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.UploadSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UploadSessionRepo extends JpaRepository<UploadSession, UUID> {
    List<UploadSession> findAllByExpiredAtLessThan(OffsetDateTime now);
}
