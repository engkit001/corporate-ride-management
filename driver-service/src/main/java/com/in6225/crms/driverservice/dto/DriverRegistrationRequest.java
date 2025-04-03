package com.in6225.crms.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequest {
    private String id; // DRIVER001, DRIVER002
    private String name;
    private String phoneNumber;
}

