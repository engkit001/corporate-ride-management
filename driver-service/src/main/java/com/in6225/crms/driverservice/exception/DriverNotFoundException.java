package com.in6225.crms.driverservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DriverNotFoundException extends RuntimeException {
     public DriverNotFoundException(String message) {
        super("Driver Not Found Exception: " + message);
    }
}
