package com.hoquangnam45.pharmacy.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties("pharma.mail")
public class EmailConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean mock;
}
