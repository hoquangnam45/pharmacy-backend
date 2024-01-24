package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.exception.UploadSessionInvalidException;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;


@ControllerAdvice
public class ControllerErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ApiError.class)
    public ResponseEntity<Object> handleApiError(ApiError err, WebRequest webRequest) {
        String path = ((ServletWebRequest) webRequest).getRequest().getServletPath();
        return ResponseEntity.status(err.getHttpStatus()).body(new GenericResponse(err.getHttpStatus(), path, err.getMessage(), err.getDetail()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericResponse> handleAccessDenied(AccessDeniedException err, WebRequest webRequest) {
        String path = ((ServletWebRequest) webRequest).getRequest().getServletPath();
        return ResponseEntity.status(403).body(new GenericResponse(403, path, err.getMessage(), null));
    }

    @ExceptionHandler(UploadSessionInvalidException.class)
    public ResponseEntity<GenericResponse> handleUploadSessionInvalid(UploadSessionInvalidException err, WebRequest webRequest) {
        String path = ((ServletWebRequest) webRequest).getRequest().getServletPath();
        return ResponseEntity.status(400).body(new GenericResponse(400, path, err.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GenericResponse> handleGenericException(Exception err, WebRequest webRequest) throws Exception {
        String path = ((ServletWebRequest) webRequest).getRequest().getServletPath();
        return ResponseEntity.status(500).body(new GenericResponse(500, path, "Internal server error", err.getClass().getSimpleName()));
    }
}
