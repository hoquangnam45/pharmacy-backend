package com.hoquangnam45.pharmacy.repo.audit;

import com.hoquangnam45.pharmacy.entity.DeliveryInfoAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryInfoAuditRepo extends AuditRepo<DeliveryInfoAudit, UUID> {
}
