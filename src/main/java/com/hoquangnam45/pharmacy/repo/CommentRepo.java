package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {
    Optional<Comment> findByCreatedByAndId(UUID userId, UUID commentId);
}
