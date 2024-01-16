package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartRepo extends JpaRepository<Cart, UUID> {
    Cart findCartByIdAndUser_Id(UUID cartId, UUID userId);


}
