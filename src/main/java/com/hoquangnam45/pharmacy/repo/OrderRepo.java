package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID> {
    boolean existsByListing_Id(UUID listingId);
}
