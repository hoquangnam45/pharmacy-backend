package com.hoquangnam45.pharmacy.repo.audit;

import com.hoquangnam45.pharmacy.entity.MedicineListingAudit;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicineListingAuditRepo extends AuditRepo<MedicineListingAudit, UUID> {
}
