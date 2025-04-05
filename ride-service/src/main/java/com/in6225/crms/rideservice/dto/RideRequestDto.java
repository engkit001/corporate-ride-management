package com.in6225.crms.rideservice.dto;

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
public class RideRequestDto {
    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "EMP\\d{3}", message = "User ID must start with 'EMP' followed by three digits (e.g., EMP123)")
    private String userId;

    @NotBlank(message = "Pickup location is required")
    @Size(max = 255, message = "Pickup location must be at most 255 characters")
    private String pickupLocation;

    @NotBlank(message = "Drop-off location is required")
    @Size(max = 255, message = "Drop-off location must be at most 255 characters")
    private String dropoffLocation;;
}
