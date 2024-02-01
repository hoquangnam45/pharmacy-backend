package com.hoquangnam45.pharmacy.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties("pharma.verification")
public class VerificationConfig {
    private Config email;
    private Config phoneNumber;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Config {
        private Integer expireTimeInMin;
        private Integer length;
    }
}
