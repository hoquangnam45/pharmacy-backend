package com.hoquangnam45.pharmacy.exception;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError extends RuntimeException {
    private final int httpStatus;
    private final Object detail;

    public ApiError(int httpStatus, String message, Object detail) {
        super(message);
        this.httpStatus = httpStatus;
        this.detail = detail;
    }

    public ApiError(int httpStatus, String message) {
        this(httpStatus, message, null);
    }

    public ApiError(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), message, null);
    }

    public static ApiError notFound(String message) {
        return new ApiError(HttpStatus.NOT_FOUND, message);
    }

    public static ApiError notFound(String message, Object detail) {
        return new ApiError(HttpStatus.NOT_FOUND.value(), message, detail);
    }

    public static ApiError forbidden(String message) {
        return new ApiError(HttpStatus.FORBIDDEN, message);
    }

    public static ApiError unauthorized(String message) {
        return new ApiError(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiError conflict(String message) {
        return new ApiError(HttpStatus.CONFLICT, message);
    }

    public static ApiError badRequest(String message) {
        return new ApiError(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiError internalServerError(String message) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static ApiError unimplemented(String message) {
        return new ApiError(HttpStatus.NOT_IMPLEMENTED, message);
    }
}
