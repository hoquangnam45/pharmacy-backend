package com.hoquangnam45.pharmacy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiError extends RuntimeException {
    private final int httpStatus;

    public ApiError(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiError(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), message);
    }

    public static ApiError notFound(String message) {
        return new ApiError(HttpStatus.NOT_FOUND, message);
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
}
