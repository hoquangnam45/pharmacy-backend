package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UploadSessionCreateResponse {
    private final UUID id;
    private final String type;
    private final Set<UUID> itemIds;
}
