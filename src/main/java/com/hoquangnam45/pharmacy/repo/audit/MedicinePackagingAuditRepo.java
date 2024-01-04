package com.hoquangnam45.pharmacy.repo.audit;

import com.hoquangnam45.pharmacy.entity.MedicinePackagingAudit;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicinePackagingAuditRepo extends AuditRepo<MedicinePackagingAudit, UUID> {
}
