package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.component.mapper.CommentMapper;
import com.hoquangnam45.pharmacy.pojo.CommentResponse;
import com.hoquangnam45.pharmacy.pojo.CreateCommentRequest;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.UpdateCommentRequest;
import com.hoquangnam45.pharmacy.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("comments")
@Transactional
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(CreateCommentRequest request) {
        return Optional.of(commentService.createComment(request))
                .map(commentMapper::createResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalStateException("Not possible to enter here"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<GenericResponse> deleteComment(
            @PathVariable("id") UUID commentId,
            HttpServletRequest request) {
        commentService.softDelete(commentId);
        String path = request.getServletPath();
        return ResponseEntity.ok(new GenericResponse(200, path, "Delete success"));
    }

    @PutMapping("{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("id") UUID commentId,
            UpdateCommentRequest request) {
        return Optional.of(commentService.updateComment(commentId, request))
                .map(commentMapper::createResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalStateException("Not possible to enter here"));
    }
}
