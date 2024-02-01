package com.hoquangnam45.pharmacy.config;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;
import com.hoquangnam45.pharmacy.service.IMailService;
import com.hoquangnam45.pharmacy.service.IS3Service;
import com.hoquangnam45.pharmacy.service.impl.MailService;
import com.hoquangnam45.pharmacy.service.impl.MockMailService;
import com.hoquangnam45.pharmacy.service.impl.MockS3Service;
import com.hoquangnam45.pharmacy.service.impl.S3Service;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class AppConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    public IS3Service s3Service(UploadConfig uploadConfig) {
        if (uploadConfig.isLocal()) {
            return new MockS3Service();
        } else {
            return new S3Service(uploadConfig);
        }
    }

    @Bean
    public JavaMailSender javaMailSender(EmailConfig emailConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());

        mailSender.setUsername(emailConfig.getUsername());
        mailSender.setPassword(emailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public IMailService mainService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        return new MockMailService(templateEngine);
    }
}
