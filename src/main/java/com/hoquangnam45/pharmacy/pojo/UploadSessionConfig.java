package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class UploadSessionConfig {
    private final int maximumFileCount;
    private final List<String> contentTypes;
    private final long maximumFileSize;
    private final int initialPlaceholderCount;
}
