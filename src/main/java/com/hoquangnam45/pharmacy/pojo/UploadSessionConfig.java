package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@AllArgsConstructor
public class UploadSessionConfig {
    private String type;
    private int maximumFileCount;
    private int expiredDurationInMin;
    private String prefix;

    public Duration getExpiredDurationInMin() {
        return Duration.ofMinutes(expiredDurationInMin);
    }
}
