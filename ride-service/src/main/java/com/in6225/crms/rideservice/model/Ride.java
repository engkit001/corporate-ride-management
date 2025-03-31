package com.in6225.crms.rideservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String status; // REQUESTED, PENDING, ONGOING, COMPLETED, CANCELLED
}