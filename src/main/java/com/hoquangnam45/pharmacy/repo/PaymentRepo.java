package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentInfo, UUID> {
    PaymentInfo findByUser_IdAndId(UUID userId, UUID paymentId);
}
