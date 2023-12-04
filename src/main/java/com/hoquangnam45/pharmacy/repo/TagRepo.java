package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepo extends JpaRepository<Tag, UUID> {
    List<Tag> findByValueIn(List<String> tags);
    List<Tag> findAllByValueLike(String query, Pageable pageable);
}
