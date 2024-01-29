package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.entity.Comment;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.CreateCommentRequest;
import com.hoquangnam45.pharmacy.pojo.UpdateCommentRequest;
import com.hoquangnam45.pharmacy.repo.CommentRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepo commentRepo;

    public CommentService(CommentRepo commentRepo) {
        this.commentRepo = commentRepo;
    }

    public Comment createComment(CreateCommentRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        return commentRepo.save(Comment.builder()
                .createdBy(userId)
                .medicineId(request.getMedicineId())
                .superCommentId(request.getSuperCommentId())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .content(request.getContent())
                .build());
    }

    public Comment updateComment(UUID commentId, UpdateCommentRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Comment comment = commentRepo.findByCreatedByAndId(userId, commentId)
                .orElseThrow(() -> ApiError.notFound("Not found comment " + commentId));
        comment.setContent(request.getContent());
        comment.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return comment;
    }

    public void softDelete(UUID commentId) {
        commentRepo.findById(commentId)
                .ifPresent(comment -> comment.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC)));
    }
}
