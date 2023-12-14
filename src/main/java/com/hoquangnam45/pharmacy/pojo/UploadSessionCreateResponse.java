package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class UploadSessionCreateResponse {
    private final String sessionId;
    private final Set<String> itemIds;
}
