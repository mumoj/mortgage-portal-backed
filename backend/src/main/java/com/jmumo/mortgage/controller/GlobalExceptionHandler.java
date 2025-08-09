package com.jmumo.mortgage.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {

        String error = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst().orElse("Validation failed");

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(LocalDateTime.now(), getPath(request), "VALIDATION_ERROR", error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccess(AccessDeniedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(LocalDateTime.now(), getPath(request), "ACCESS_DENIED", "Access denied"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(LocalDateTime.now(), getPath(request), "ERROR", ex.getMessage()));
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String path;
        private String errorCode;
        private String detail;
    }
}