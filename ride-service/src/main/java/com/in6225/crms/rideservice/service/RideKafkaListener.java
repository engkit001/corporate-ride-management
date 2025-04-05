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
        String[] parts = message.split(":");
        DriverAssignedEvent driverAssignedEvent = new DriverAssignedEvent(Long.valueOf(parts[0]), parts[1]);
        rideService.handleDriverAssignedEvent(driverAssignedEvent);
    }

    @KafkaListener(topics = "no-driver-available", groupId = "ride-service-group")
    public void handleNoDriverAvailableEvent(String message) {
        NoDriverAvailableEvent noDriverAvailableEvent = new NoDriverAvailableEvent(Long.valueOf(message));
        rideService.handleNoDriverAvailableEvent(noDriverAvailableEvent);
    }
}
