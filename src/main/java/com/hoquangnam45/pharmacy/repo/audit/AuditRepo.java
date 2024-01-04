package com.hoquangnam45.pharmacy.repo.audit;

import org.hibernate.sql.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

@NoRepositoryBean
public interface AuditRepo<T, K> extends JpaRepository<T, K> {
    T findByAuditInfo_AuditObjectIdAndCreatedAt(String auditObjectId, OffsetDateTime createdAt);

    @Modifying
    @Query("UPDATE #{#entityName} e SET e.auditInfo.active = false WHERE e.auditInfo.active = true AND e.auditInfo.auditObjectId = :auditObjectId")
    void disableAllActiveAuditObject(String auditObjectId);
}
