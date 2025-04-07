package com.in6225.crms.driverservice.service;

import com.in6225.crms.rideevents.RideCancelledEvent;
import com.in6225.crms.rideevents.RideCompletedEvent;
import com.in6225.crms.rideevents.RideRequestedEvent;
import com.in6225.crms.rideevents.RideStartedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DriverKafkaListener {
    private final DriverService driverService;

    @KafkaListener(topics = "ride-requested", groupId = "driver-service-group")
    public void handleRideRequestedEvent(String message) {
        RideRequestedEvent rideRequestedEvent = RideRequestedEvent.fromJson(message);
        driverService.handleRideRequestedEvent(rideRequestedEvent);
    }

    @KafkaListener(topics = "ride-started", groupId = "driver-service-group")
    public void handleRideStartedEvent(String message) {
        RideStartedEvent rideStartedEvent = RideStartedEvent.fromJson(message);
        driverService.handleRideStartedEvent(rideStartedEvent);
    }

    @KafkaListener(topics = "ride-completed", groupId = "driver-service-group")
    public void handleRideCompletedEvent(String message) {
        RideCompletedEvent rideCompletedEvent = RideCompletedEvent.fromJson(message);
        driverService.handleRideCompletedEvent(rideCompletedEvent);
    }

    @KafkaListener(topics = "ride-cancelled", groupId = "driver-service-group")
    public void handleRideCancelledEvent(String message) {
        RideCancelledEvent rideCancelledEvent = RideCancelledEvent.fromJson(message);
        driverService.handleRideCancelledEvent(rideCancelledEvent);
    }
}