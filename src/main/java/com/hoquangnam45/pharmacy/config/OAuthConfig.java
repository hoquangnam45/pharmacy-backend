package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.pojo.FacebookAuthConfig;
import com.hoquangnam45.pharmacy.pojo.GoogleAuthConfig;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("pharma.auth")
@Getter
public class OAuthConfig {
    private String applicationUrl;
    private GoogleAuthConfig google;
    private FacebookAuthConfig facebook;
}
