package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, UUID> {
    List<CartItem> findAllByUser_IdAndIdIn(UUID userId, List<UUID> cartItemIds);
    CartItem findByListingIdAndUser_Id(UUID listingId, UUID cartId);
    void deleteAllByUser_IdAndIdIn(UUID userId, List<UUID> cartItemIds);

    @Query("SELECT ci FROM CartItem ci INNER JOIN FETCH ci.listing l INNER JOIN FETCH l.packaging p INNER JOIN FETCH p.medicine m INNER JOIN FETCH m.previews previews INNER JOIN FETCH previews.fileMetadata WHERE ci.user.id = :userId")
    List<CartItem> findCartItemUserIdFetched(UUID userId);
}
