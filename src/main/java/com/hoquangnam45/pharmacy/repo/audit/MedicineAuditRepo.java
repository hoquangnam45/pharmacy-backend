package com.hoquangnam45.pharmacy.repo.audit;

import com.hoquangnam45.pharmacy.entity.MedicineAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicineAuditRepo extends AuditRepo<MedicineAudit, UUID> {
}
