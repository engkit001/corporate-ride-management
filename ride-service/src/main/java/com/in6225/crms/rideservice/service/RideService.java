package com.in6225.crms.rideservice.service;

import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.in6225.crms.rideevents.NoDriverAvailableEvent;
import com.in6225.crms.rideservice.dto.RideDto;
import com.in6225.crms.rideservice.dto.RideRequestDto;

import java.util.List;

public interface RideService {
    RideDto getRideById(Long id);

    List<RideDto> getRidesByUserIdAndStatus(String userId, String status);

    RideDto requestRide(RideRequestDto rideRequestDto);

    RideDto startRide(Long id);

    RideDto completeRide(Long id);

    RideDto cancelRide(Long id);

    void handleDriverAssignedEvent(DriverAssignedEvent driverAssignedEvent);

    void handleNoDriverAvailableEvent(NoDriverAvailableEvent noDriverAvailableEvent);
}
