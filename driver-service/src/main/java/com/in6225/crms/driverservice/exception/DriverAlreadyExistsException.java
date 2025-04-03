package com.in6225.crms.driverservice.exception;

public class DriverAlreadyExistsException extends RuntimeException {
    public DriverAlreadyExistsException(String message) {
        super(message);
    }
}
