package com.hoquangnam45.pharmacy.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
@ConfigurationProperties("pharma.order")
public class OrderConfig {
    private Integer expiredDurationInDays;

    private Duration getExpiredDurationInDays() {
        return Optional.ofNullable(expiredDurationInDays)
                .map(Duration::ofDays)
                .orElseGet(() -> Duration.ofDays(3L));
    }
}
