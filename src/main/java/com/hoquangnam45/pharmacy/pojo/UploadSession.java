package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class UploadSession {
    private final String sessionId;
    private final Set<String> allowedFileIds;
    private final OffsetDateTime expiredAt;
    private final Duration expiredDuration;
}
