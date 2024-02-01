package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.pojo.SendEmailRequest;
import com.hoquangnam45.pharmacy.pojo.SendEmailTemplateRequest;
import com.hoquangnam45.pharmacy.service.IMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.MessageFormat;
import java.util.Optional;

public class MockMailService implements IMailService {
    private final Logger logger = LoggerFactory.getLogger(MockMailService.class);
    private final TemplateEngine templateEngine;

    public MockMailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(SendEmailTemplateRequest request) throws Exception {
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

    @Override
    public void sendEmail(SendEmailRequest request) {
        logger.info(MessageFormat.format("Sent email to [{0}] content [{1}]", request.getToAddress(), request.getContent()));
    }
}
