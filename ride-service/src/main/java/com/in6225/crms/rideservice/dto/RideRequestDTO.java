package com.in6225.crms.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDTO {
    private String userId;
    private String pickupLocation;
    private String dropoffLocation;
}
