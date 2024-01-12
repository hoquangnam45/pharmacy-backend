package com.hoquangnam45.pharmacy.repo.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.OffsetDateTime;

@NoRepositoryBean
public interface AuditRepo<T, K> extends JpaRepository<T, K> {
    T findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(String auditObjectId, OffsetDateTime createdAt);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.auditInfo.active = false WHERE e.auditInfo.active = true AND e.auditInfo.auditObjectId = :auditObjectId")
    void disableAllActiveAuditObject(String auditObjectId);
}
