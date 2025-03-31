package com.in6225.crms.driverservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DriverNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DriverNotFoundException(String id) {
        super("Driver Not Found Exception: " + id);
    }
}
