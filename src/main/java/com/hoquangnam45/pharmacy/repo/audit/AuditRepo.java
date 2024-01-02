package com.hoquangnam45.pharmacy.repo.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepo<T, K> extends JpaRepository<T, K> {
    T findByAuditInfo_AuditObjectId(String auditObjectId);
}
