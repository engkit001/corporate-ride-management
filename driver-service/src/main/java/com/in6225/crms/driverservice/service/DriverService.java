package com.in6225.crms.driverservice.service;

import com.in6225.crms.driverservice.exception.DriverNotFoundException;
import com.in6225.crms.driverservice.model.Driver;
import com.in6225.crms.driverservice.repository.DriverRepository;
import com.in6225.crms.rideevents.RideCompletedEvent;
import com.in6225.crms.rideevents.RideRequestedEvent;
import com.in6225.crms.rideevents.RideStartedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public DriverService(DriverRepository driverRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.driverRepository = driverRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Get drivers by status
    public List<Driver> getDriversByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return driverRepository.findAll(); // Return all drivers if no status is provided
        }
        return driverRepository.findAllByStatus(status);
    }

    // Register a new driver
    public Driver registerDriver(Driver driver) {
        driver.setStatus("AVAILABLE");
        return driverRepository.save(driver);
    }

    public void handleRideRequestedEvent(RideRequestedEvent rideRequestedEvent) {
        Optional<Driver> availableDriver = driverRepository.findFirstByStatus("AVAILABLE");

        if (availableDriver.isPresent()) {
            Driver driver = availableDriver.get();
            driver.setStatus("ASSIGNED");
            driverRepository.save(driver);

            // Publish DRIVER_ASSIGNED event
            kafkaTemplate.send("driver-assigned", rideRequestedEvent.getRideId() + ":" + driver.getId());
        } else {
            // Publish NO_DRIVER_AVAILABLE event
            kafkaTemplate.send("no-driver-available", String.valueOf(rideRequestedEvent.getRideId()));
        }
    }

    public void handleRideStartedEvent(RideStartedEvent rideStartedEvent) {
        Optional<Driver> driverOptional = driverRepository.findById(rideStartedEvent.getDriverId());
        if (driverOptional.isPresent()) {
            Driver driver = driverOptional.get();
            driver.setStatus("BUSY");
            driverRepository.save(driver);

        }
        throw new DriverNotFoundException(rideStartedEvent.getDriverId());
    }

    public void handleRideCompletedEvent(RideCompletedEvent rideCompletedEvent) {
        Optional<Driver> driverOptional = driverRepository.findById(rideCompletedEvent.getDriverId());
        if (driverOptional.isPresent()) {
            Driver driver = driverOptional.get();
            driver.setStatus("AVAILABLE");
            driverRepository.save(driver);

        }
        throw new DriverNotFoundException(rideCompletedEvent.getDriverId());
    }

}
