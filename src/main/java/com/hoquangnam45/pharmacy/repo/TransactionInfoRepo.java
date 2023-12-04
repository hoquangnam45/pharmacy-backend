package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionInfoRepo extends JpaRepository<TransactionInfo, UUID> {
}
