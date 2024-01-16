package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.MedicineListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineListingRepo extends JpaRepository<MedicineListing, UUID> {
    void deleteByPackaging_Id(UUID id);
    void deleteAllByPackaging_Medicine_Id(UUID id);
    Optional<MedicineListing> findByIdAndDisableFalse(UUID id);
    List<MedicineListing> findAllByIdIn(Collection<UUID> listingIds);

    @Query(value = "SELECT l FROM MedicineListing l INNER JOIN FETCH l.packaging p INNER JOIN FETCH p.medicine m INNER JOIN FETCH m.previews previews INNER JOIN FETCH previews.fileMetadata WHERE l.id = :id")
    MedicineListing findListingFullyFetchedById(UUID id);
}
