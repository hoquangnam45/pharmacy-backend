package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.MedicineListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineListingRepo extends JpaRepository<MedicineListing, UUID> {
    void deleteByPackaging_Id(UUID id);
    void deleteAllByPackaging_Medicine_Id(UUID id);
    Optional<MedicineListing> findByIdAndDisableFalse(UUID id);
}
