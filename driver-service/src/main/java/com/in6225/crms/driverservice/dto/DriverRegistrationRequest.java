package com.in6225.crms.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationRequest {
    @NotBlank(message = "Driver ID is required")
    @Pattern(regexp = "DRIVER\\d{3}", message = "Driver ID must start with 'DRIVER' followed by three digits (e.g., DRIVER001)")
    private String id; // DRIVER001, DRIVER002

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{8}$", message = "Phone number must be exactly 8 digits")
    private String phoneNumber;
}

