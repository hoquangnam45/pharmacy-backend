package com.hoquangnam45.pharmacy.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Getter
public class GenericResponse {
    private final String path;
    private final String msg;
    private final int status;
    @JsonInclude(Include.NON_NULL)
    private final Object details;
    private final OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

    public GenericResponse(int status, String path, String msg, Object details) {
        this.path = path;
        this.msg = msg;
        this.status = status;
        this.details = details;
    }

    public GenericResponse(int status, String path, String msg) {
        this(status, path, msg, null);
    }

    public GenericResponse(HttpStatus httpStatus, String path, String msg) {
        this(httpStatus.value(), path, msg);
    }

    public GenericResponse(HttpStatus httpStatus, String path, String msg, Object details) {
        this(httpStatus.value(), path, msg, details);
    }
}

