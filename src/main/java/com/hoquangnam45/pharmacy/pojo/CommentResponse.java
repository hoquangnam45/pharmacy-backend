package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter
@RequiredArgsConstructor
public class CommentResponse {
    private final UUID id;
    private final UUID superCommentId;
    private final UUID medicineId;
    private final String content;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
