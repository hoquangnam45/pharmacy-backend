package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.pojo.SendEmailRequest;
import com.hoquangnam45.pharmacy.pojo.SendEmailTemplateRequest;
import jakarta.mail.MessagingException;

public interface IMailService {
    void sendEmail(SendEmailTemplateRequest request) throws Exception;
    void sendEmail(SendEmailRequest request) throws Exception;
}
