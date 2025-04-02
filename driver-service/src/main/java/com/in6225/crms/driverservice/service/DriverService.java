package com.in6225.crms.driverservice.service;

import com.in6225.crms.driverservice.enums.DriverStatus;
import com.in6225.crms.driverservice.exception.DriverNotFoundException;
import com.in6225.crms.driverservice.entity.Driver;
import com.in6225.crms.driverservice.exception.InvalidDriverStateException;
import com.in6225.crms.driverservice.repository.DriverRepository;
import com.in6225.crms.rideevents.RideCancelledEvent;
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

    // Get driver by id
    public Driver getDriverById(String id) {
        Optional<Driver> driverOptional = driverRepository.findById(id);

        if (driverOptional.isEmpty()) {
            throw new DriverNotFoundException(id);
        }

        return driverOptional.get();
    }

    // Get drivers by status
    public List<Driver> getDriversByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return driverRepository.findAll(); // Return all drivers if no status is provided
        }
        DriverStatus driverStatus = DriverStatus.valueOf(status);
        return driverRepository.findAllByStatus(driverStatus);
    }

    // Register a new driver
    public Driver registerDriver(Driver driver) {
        driver.setStatus(DriverStatus.AVAILABLE);
        return driverRepository.save(driver);
    }

    public void handleRideRequestedEvent(RideRequestedEvent rideRequestedEvent) {
        Optional<Driver> availableDriver = driverRepository.findFirstByStatus(DriverStatus.AVAILABLE);

        if (availableDriver.isPresent()) {
            Driver driver = availableDriver.get();
            driver.setStatus(DriverStatus.ASSIGNED);
            driverRepository.save(driver);

            // Publish DRIVER_ASSIGNED event
            kafkaTemplate.send("driver-assigned", rideRequestedEvent.getRideId() + ":" + driver.getId());
        } else {
            // Publish NO_DRIVER_AVAILABLE event
            kafkaTemplate.send("no-driver-available", String.valueOf(rideRequestedEvent.getRideId()));
        }
    }

    public void handleRideStartedEvent(RideStartedEvent rideStartedEvent) {
        Driver driver = this.getDriverById(rideStartedEvent.getDriverId());
        if (driver.getStatus() != DriverStatus.ASSIGNED) {
            throw new InvalidDriverStateException("Driver is not ASSIGNED");
        }
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
    }

    public void handleRideCompletedEvent(RideCompletedEvent rideCompletedEvent) {
        Driver driver = this.getDriverById(rideCompletedEvent.getDriverId());
        if (driver.getStatus() != DriverStatus.BUSY) {
            throw new InvalidDriverStateException("Driver is not BUSY");
        }
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
    }

    public void handleRideCancelledEvent(RideCancelledEvent rideCancelledEvent) {
        Driver driver = this.getDriverById(rideCancelledEvent.getDriverId());
        if (driver.getStatus() != DriverStatus.ASSIGNED) {
            throw new InvalidDriverStateException("Driver is not ASSIGNED");
        }
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
    }

}
