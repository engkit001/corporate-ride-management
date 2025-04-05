package com.in6225.crms.driverservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom exceptions
    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDriverNotFoundException(DriverNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Driver Not Found", ex.getMessage());
    }

    @ExceptionHandler(InvalidDriverStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDriverStateException(InvalidDriverStateException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Driver State", ex.getMessage());
    }

    @ExceptionHandler(DriverAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleDriverAlreadyExistsException(DriverAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Driver Already Exists", ex.getMessage());
    }

    // Build a consistent error response format
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
}
