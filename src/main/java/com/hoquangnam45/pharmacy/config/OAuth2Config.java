package com.hoquangnam45.pharmacy.config;

import com.hoquangnam45.pharmacy.pojo.FacebookOAuth2Config;
import com.hoquangnam45.pharmacy.pojo.GoogleOAuth2Config;
import com.hoquangnam45.pharmacy.service.impl.FacebookOAuth2Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("pharma.oauth2")
@Getter
@Setter
public class OAuth2Config {
    private GoogleOAuth2Config google;
    private FacebookOAuth2Config facebook;
}
