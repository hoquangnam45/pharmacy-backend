package com.hoquangnam45.pharmacy.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
@ConfigurationProperties("pharma.jwt")
public class JwtConfig {
    private String secret;
    private String issuer;
    private Integer refreshTokenExpirationInMin;
    private Integer accessTokenExpirationInMin;

    public Duration getRefreshTokenExpirationInMin() {
        return Duration.of(refreshTokenExpirationInMin, ChronoUnit.MINUTES);
    }

    public Duration getAccessTokenExpirationInMin() {
        return Duration.of(accessTokenExpirationInMin, ChronoUnit.MINUTES);
    }
}
