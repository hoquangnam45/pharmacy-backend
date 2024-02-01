package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Builder
public class SendEmailTemplateRequest {
    private final String toAddress;
    private final String viewName;
    private final Map<String, Object> parameters;
    private final Map<String, File> attachments;
    private final Locale locale;
}
