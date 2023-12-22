package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicinePackagingRepo extends JpaRepository<MedicinePackaging, UUID> {
    void deleteAllByMedicine_Id(UUID id);
}
