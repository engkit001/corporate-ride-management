package com.in6225.crms.driverservice.service.impl;

import com.in6225.crms.driverservice.dto.DriverDto;
import com.in6225.crms.driverservice.entity.Driver;
import com.in6225.crms.driverservice.enums.DriverStatus;
import com.in6225.crms.driverservice.exception.DriverAlreadyExistsException;
import com.in6225.crms.driverservice.exception.DriverNotFoundException;
import com.in6225.crms.driverservice.exception.InvalidDriverStateException;
import com.in6225.crms.driverservice.repository.DriverRepository;
import com.in6225.crms.driverservice.service.DriverService;
import com.in6225.crms.rideevents.*;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public DriverDto getDriverById(String id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));
        return mapToDto(driver);
    }

    @Override
    public List<DriverDto> getDriversByStatus(String status) {
        List<Driver> driverList;

        // Get all drivers if no status is provided
        if (status == null || status.isEmpty()) {
            driverList = driverRepository.findAll();
        }
        else {
            driverList = driverRepository.findAllByStatus(DriverStatus.valueOf(status));
        }

        List<DriverDto> driverDtoList = new ArrayList<>();
        for (Driver driver : driverList) {
            DriverDto driverDto = mapToDto(driver);
            driverDtoList.add(driverDto);
        }
        return driverDtoList;
    }

    @Override
    public DriverDto registerDriver(DriverDto driverDto) {
        String driverId = driverDto.getId();
        if (driverRepository.existsById(driverId)) {
            throw new DriverAlreadyExistsException(driverId);
        }

        Driver driver = new Driver(
                driverId,
                driverDto.getName(),
                driverDto.getPhoneNumber(),
                driverDto.getVehicleNumber(),
                DriverStatus.AVAILABLE
        );
        Driver savedDriver = driverRepository.save(driver);

        return mapToDto(savedDriver);
    }

    private DriverDto mapToDto(Driver driver) {
        return new DriverDto(
                driver.getId(),
                driver.getName(),
                driver.getPhoneNumber(),
                driver.getVehicleNumber(),
                driver.getStatus()
        );
    }

    @Override
    public void handleRideRequestedEvent(RideRequestedEvent rideRequestedEvent) {
        Optional<Driver> availableDriver = driverRepository.findFirstByStatus(DriverStatus.AVAILABLE);

        if (availableDriver.isPresent()) {
            Driver driver = availableDriver.get();
            driver.setStatus(DriverStatus.ASSIGNED);
            driverRepository.save(driver);

            // Publish DRIVER_ASSIGNED event
            DriverAssignedEvent driverAssignedEvent = new DriverAssignedEvent(
                    rideRequestedEvent.getRideId(),
                    driver.getId(),
                    driver.getPhoneNumber(),
                    driver.getVehicleNumber()
            );
            kafkaTemplate.send("driver-assigned", driverAssignedEvent.toJson());
        } else {
            // Publish NO_DRIVER_AVAILABLE event
            NoDriverAvailableEvent noDriverAvailableEvent = new NoDriverAvailableEvent(
                    rideRequestedEvent.getRideId()
            );
            kafkaTemplate.send("no-driver-available", noDriverAvailableEvent.toJson());
        }
    }

    @Override
    public void handleRideStartedEvent(RideStartedEvent rideStartedEvent) {
        String driverId = rideStartedEvent.getDriverId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        if (driver.getStatus() != DriverStatus.ASSIGNED) {
            throw new InvalidDriverStateException("Driver is not ASSIGNED");
        }

        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
    }

    @Override
    public void handleRideCompletedEvent(RideCompletedEvent rideCompletedEvent) {
        String driverId = rideCompletedEvent.getDriverId();
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        if (driver.getStatus() != DriverStatus.BUSY) {
            throw new InvalidDriverStateException("Driver is not BUSY");
        }

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
    }

    @Override
    public void handleRideCancelledEvent(RideCancelledEvent rideCancelledEvent) {
        String driverId = rideCancelledEvent.getDriverId();
        if (driverId.isEmpty()) {
            return;
        }
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        if (driver.getStatus() != DriverStatus.ASSIGNED) {
            throw new InvalidDriverStateException("Driver is not ASSIGNED");
        }

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);
    }
}
