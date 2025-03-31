package com.in6225.crms.rideservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RideNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RideNotFoundException(Long id) {
        super("Ride Not Found Exception: " + id);
    }
}
