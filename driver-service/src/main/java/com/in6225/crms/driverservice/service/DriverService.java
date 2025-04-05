package com.in6225.crms.driverservice.service;

import com.in6225.crms.driverservice.dto.DriverDto;
import com.in6225.crms.rideevents.RideCancelledEvent;
import com.in6225.crms.rideevents.RideCompletedEvent;
import com.in6225.crms.rideevents.RideRequestedEvent;
import com.in6225.crms.rideevents.RideStartedEvent;

import java.util.List;

public interface DriverService {
    DriverDto getDriverById(String id);

    List<DriverDto> getDriversByStatus(String status);

    DriverDto registerDriver(DriverDto driverDto);

    void handleRideRequestedEvent(RideRequestedEvent rideRequestedEvent);

    void handleRideStartedEvent(RideStartedEvent rideStartedEvent);

    void handleRideCompletedEvent(RideCompletedEvent rideCompletedEvent);

    void handleRideCancelledEvent(RideCancelledEvent rideCancelledEvent);
}