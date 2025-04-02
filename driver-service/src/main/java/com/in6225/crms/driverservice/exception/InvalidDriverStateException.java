package com.in6225.crms.driverservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDriverStateException extends RuntimeException {
    public InvalidDriverStateException(String message) {
        super(message);
    }
}