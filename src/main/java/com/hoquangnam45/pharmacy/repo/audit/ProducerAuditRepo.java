package com.hoquangnam45.pharmacy.repo.audit;

import com.hoquangnam45.pharmacy.entity.ProducerAudit;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProducerAuditRepo extends AuditRepo<ProducerAudit, UUID> {
}
