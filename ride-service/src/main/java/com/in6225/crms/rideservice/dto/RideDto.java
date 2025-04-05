package com.in6225.crms.rideservice.dto;

import com.in6225.crms.rideservice.enums.RideStatus;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideDto {
    @Id
    private Long id;
    private String userId;
    private String driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime rideRequestedTime;
    private LocalDateTime rideAssignedTime;
    private LocalDateTime rideStartTime;
    private LocalDateTime rideEndTime;
    private LocalDateTime rideCanceledTime;
    private RideStatus status;
}