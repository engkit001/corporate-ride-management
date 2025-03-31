package com.in6225.crms.driverservice.service;

import com.in6225.crms.rideevents.RideCompletedEvent;
import com.in6225.crms.rideevents.RideRequestedEvent;
import com.in6225.crms.rideevents.RideStartedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DriverKafkaListener {
    private final DriverService driverService;

    public DriverKafkaListener(DriverService driverService) {
        this.driverService = driverService;
    }

    @KafkaListener(topics = "ride-requested", groupId = "driver-service-group")
    public void handleRideRequestedEvent(String message) {
        RideRequestedEvent rideRequestedEvent = new RideRequestedEvent(Long.valueOf(message));
        driverService.handleRideRequestedEvent(rideRequestedEvent);
    }

    @KafkaListener(topics = "ride-started", groupId = "driver-service-group")
    public void handleRideStartedEvent(String message) {
        String[] parts = message.split(":");
        RideStartedEvent rideStartedEvent = new RideStartedEvent(Long.valueOf(parts[0]),parts[1]);
        driverService.handleRideStartedEvent(rideStartedEvent);
    }

    @KafkaListener(topics = "ride-completed", groupId = "driver-service-group")
    public void handleRideCompletedEvent(String message) {
        String[] parts = message.split(":");
        RideCompletedEvent rideCompletedEvent = new RideCompletedEvent(Long.valueOf(parts[0]),parts[1]);
        driverService.handleRideCompletedEvent(rideCompletedEvent);
    }

}