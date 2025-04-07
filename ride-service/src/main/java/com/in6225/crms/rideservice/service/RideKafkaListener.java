package com.in6225.crms.rideservice.service;

import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.in6225.crms.rideevents.NoDriverAvailableEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RideKafkaListener {
    private final RideService rideService;

    @KafkaListener(topics = "driver-assigned", groupId = "ride-service-group")
    public void handleDriverAssignedEvent(String message) {
        DriverAssignedEvent driverAssignedEvent = DriverAssignedEvent.fromJson(message);
        rideService.handleDriverAssignedEvent(driverAssignedEvent);
    }

    @KafkaListener(topics = "no-driver-available", groupId = "ride-service-group")
    public void handleNoDriverAvailableEvent(String message) {
        NoDriverAvailableEvent noDriverAvailableEvent = NoDriverAvailableEvent.fromJson(message);
        rideService.handleNoDriverAvailableEvent(noDriverAvailableEvent);
    }
}
