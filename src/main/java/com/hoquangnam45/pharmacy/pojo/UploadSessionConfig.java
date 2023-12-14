package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public class UploadSessionConfig {
    private final int maximumFileCount;
    private final Duration expiredDuration;
}
