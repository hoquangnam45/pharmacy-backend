package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.DeliveryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryInfoRepo extends JpaRepository<DeliveryInfo, UUID> {
}
