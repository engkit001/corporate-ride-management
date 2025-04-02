package com.in6225.crms.driverservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDriverNotFoundException(DriverNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Driver Not Found", ex.getMessage());
    }

    @ExceptionHandler(InvalidDriverStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDriverStateException(InvalidDriverStateException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Driver State", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
