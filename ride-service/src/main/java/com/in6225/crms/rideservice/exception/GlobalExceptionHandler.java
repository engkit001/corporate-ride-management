package com.in6225.crms.rideservice.exception;

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

    @ExceptionHandler(InvalidRideStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRideStateException(InvalidRideStateException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Ride State", ex.getMessage());
    }

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRideNotFoundException(RideNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Ride Not Found", ex.getMessage());
    }

    @ExceptionHandler(OpenRideExistsException.class)
    public ResponseEntity<Map<String, Object>> handleOpenRideExistsException(OpenRideExistsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Open Ride Exists", ex.getMessage());
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }
}
