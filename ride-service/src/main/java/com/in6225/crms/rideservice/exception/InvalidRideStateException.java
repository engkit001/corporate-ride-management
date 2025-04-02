package com.in6225.crms.rideservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRideStateException extends RuntimeException {
    public InvalidRideStateException(String message) {
        super(message);
    }
}

