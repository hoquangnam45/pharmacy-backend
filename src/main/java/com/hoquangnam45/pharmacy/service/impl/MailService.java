package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.pojo.SendEmailRequest;
import com.hoquangnam45.pharmacy.pojo.SendEmailTemplateRequest;
import com.hoquangnam45.pharmacy.service.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class MailService implements IMailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    public MailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(SendEmailTemplateRequest request) throws MessagingException {
        // Init thymeleaf context based on locale
        Context thymeleafContext = Optional.ofNullable(request.getLocale())
                .map(Context::new)
                .orElseGet(Context::new);

        // Process mail template with thymeleaf
        thymeleafContext.setVariables(request.getParameters());
        String content = templateEngine.process(request.getViewName(), thymeleafContext);

        sendEmail(SendEmailRequest.builder()
                .attachments(request.getAttachments())
                .toAddress(request.getToAddress())
                .content(content)
                .locale(request.getLocale())
                .build());
    }

    public void sendEmail(SendEmailRequest request) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(request.getToAddress());
        helper.setText(request.getContent());
        for (Map.Entry<String, File> entry : Optional.ofNullable(request.getAttachments())
                .orElseGet(Map::of)
                .entrySet()) {
            helper.addAttachment(entry.getKey(), entry.getValue());
        }
        javaMailSender.send(mimeMessage);
    }
}
