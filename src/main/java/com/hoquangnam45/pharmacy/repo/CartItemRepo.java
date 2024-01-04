package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, UUID> {
    List<CartItem> findAllByCart_User_IdAndIdIn(UUID userId, List<UUID> cartItemIds);
    void deleteAllByCart_User_IdAndIdIn(UUID userId, List<UUID> cartItemIds);
}
