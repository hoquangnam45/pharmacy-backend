package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class CreateCommentRequest {
    private final String content;
    private final UUID medicineId;
    private final UUID superCommentId;
}
