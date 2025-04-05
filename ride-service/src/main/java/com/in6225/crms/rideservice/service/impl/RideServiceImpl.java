package com.in6225.crms.rideservice.service.impl;

import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.in6225.crms.rideevents.NoDriverAvailableEvent;
import com.in6225.crms.rideservice.dto.RideDto;
import com.in6225.crms.rideservice.dto.RideRequestDto;
import com.in6225.crms.rideservice.enums.RideStatus;
import com.in6225.crms.rideservice.exception.InvalidRideStateException;
import com.in6225.crms.rideservice.exception.RideNotFoundException;
import com.in6225.crms.rideservice.entity.Ride;
import com.in6225.crms.rideservice.repository.RideRepository;
import com.in6225.crms.rideservice.service.RideService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final KafkaTemplate<Object, String> kafkaTemplate;

    @Override
    public RideDto getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.valueOf(id)));
        return new RideDto(
                ride.getId(),
                ride.getUserId(),
                ride.getDriverId(),
                ride.getPickupLocation(),
                ride.getDropoffLocation(),
                ride.getRideRequestedTime(),
                ride.getRideAssignedTime(),
                ride.getRideStartTime(),
                ride.getRideEndTime(),
                ride.getRideCanceledTime(),
                ride.getStatus()
        );
    }

    @Override
    public List<RideDto> getRidesByUserId(String userId) {
        List<Ride> rideList;

        // Get all drivers if no status is provided
        if (userId == null || userId.isEmpty()) {
            rideList = rideRepository.findAll();
        }
        else {
            rideList = rideRepository.findByUserId(userId);
        }

        List<RideDto> rideDtoList = new ArrayList<>();
        for (Ride ride : rideList) {
            RideDto rideDto = new RideDto(
                    ride.getId(),
                    ride.getUserId(),
                    ride.getDriverId(),
                    ride.getPickupLocation(),
                    ride.getDropoffLocation(),
                    ride.getRideRequestedTime(),
                    ride.getRideAssignedTime(),
                    ride.getRideStartTime(),
                    ride.getRideEndTime(),
                    ride.getRideCanceledTime(),
                    ride.getStatus()
            );
            rideDtoList.add(rideDto);
        }
        return rideDtoList;
    }

    @Override
    public RideDto requestRide(RideRequestDto rideRequestDto) {
        // TODO: Check for existing ride not COMPLETED or CANCELLED
        Ride ride = new Ride();
        ride.setUserId(rideRequestDto.getUserId());
        ride.setPickupLocation(rideRequestDto.getPickupLocation());
        ride.setDropoffLocation(rideRequestDto.getDropoffLocation());
        ride.setRideRequestedTime(LocalDateTime.now());
        ride.setStatus(RideStatus.REQUESTED);
        Ride savedRide = rideRepository.save(ride);

        // Publish RIDE_REQUESTED event to Kafka
        kafkaTemplate.send("ride-requested", savedRide.getId().toString());

        return new RideDto(
                savedRide.getId(),
                savedRide.getUserId(),
                savedRide.getDriverId(),
                savedRide.getPickupLocation(),
                savedRide.getDropoffLocation(),
                savedRide.getRideRequestedTime(),
                savedRide.getRideAssignedTime(),
                savedRide.getRideStartTime(),
                savedRide.getRideEndTime(),
                savedRide.getRideCanceledTime(),
                savedRide.getStatus()
        );
    }

    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
    public void processPendingRides() {
        List<Ride> pendingRides = rideRepository.findAllByStatus(RideStatus.PENDING);

        for (Ride ride : pendingRides) {
            // Publish RIDE_REQUESTED event to Kafka
            kafkaTemplate.send("ride-requested", ride.getId().toString());
        }
    }

    @Override
    public RideDto startRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.valueOf(id)));
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStateException("Ride must be ASSIGNED before starting");
        }
        ride.setStatus(RideStatus.ONGOING);
        ride.setRideStartTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        // Publish RIDE_STARTED event to Kafka
        kafkaTemplate.send("ride-started", ride.getId() + ":" + ride.getDriverId());

        return new RideDto(
                savedRide.getId(),
                savedRide.getUserId(),
                savedRide.getDriverId(),
                savedRide.getPickupLocation(),
                savedRide.getDropoffLocation(),
                savedRide.getRideRequestedTime(),
                savedRide.getRideAssignedTime(),
                savedRide.getRideStartTime(),
                savedRide.getRideEndTime(),
                savedRide.getRideCanceledTime(),
                savedRide.getStatus()
        );
    }

    @Override
    public RideDto completeRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.valueOf(id)));
        if (ride.getStatus() != RideStatus.ONGOING) {
            throw new InvalidRideStateException("Ride must be ONGOING before completing");
        }
        ride.setRideEndTime(LocalDateTime.now());
        ride.setStatus(RideStatus.COMPLETED);
        Ride savedRide = rideRepository.save(ride);

        // Publish RIDE_COMPLETED event to Kafka
        kafkaTemplate.send("ride-completed", ride.getId() + ":" + ride.getDriverId());

        return new RideDto(
                savedRide.getId(),
                savedRide.getUserId(),
                savedRide.getDriverId(),
                savedRide.getPickupLocation(),
                savedRide.getDropoffLocation(),
                savedRide.getRideRequestedTime(),
                savedRide.getRideAssignedTime(),
                savedRide.getRideStartTime(),
                savedRide.getRideEndTime(),
                savedRide.getRideCanceledTime(),
                savedRide.getStatus()
        );
    }

    @Override
    public RideDto cancelRide(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(String.valueOf(id)));
        switch (ride.getStatus()) {
            case PENDING, ASSIGNED -> {
                ride.setRideCanceledTime(LocalDateTime.now());
                ride.setStatus(RideStatus.CANCELLED);
                Ride savedRide = rideRepository.save(ride);

                // Publish RIDE_STARTED event to Kafka
                kafkaTemplate.send("ride-cancelled", ride.getId() + ":" + ride.getDriverId());

                return new RideDto(
                        savedRide.getId(),
                        savedRide.getUserId(),
                        savedRide.getDriverId(),
                        savedRide.getPickupLocation(),
                        savedRide.getDropoffLocation(),
                        savedRide.getRideRequestedTime(),
                        savedRide.getRideAssignedTime(),
                        savedRide.getRideStartTime(),
                        savedRide.getRideEndTime(),
                        savedRide.getRideCanceledTime(),
                        savedRide.getStatus()
                );
            }
            // REQUESTED, ONGOING, COMPLETED, CANCELLED
            default ->
                    throw new InvalidRideStateException("Ride cannot be cancelled: " + ride.getStatus());
        }
    }

    @Override
    public void handleDriverAssignedEvent(DriverAssignedEvent driverAssignedEvent) {
        Long rideId = driverAssignedEvent.getRideId();
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(String.valueOf(rideId)));
        if (ride.getStatus() != RideStatus.REQUESTED && ride.getStatus() != RideStatus.PENDING) {
            throw new InvalidRideStateException("Ride must be REQUESTED/PENDING before assigning");
        }
        ride.setDriverId(driverAssignedEvent.getDriverId());
        ride.setRideAssignedTime(LocalDateTime.now());
        ride.setStatus(RideStatus.ASSIGNED);
        rideRepository.save(ride);
    }

    @Override
    public void handleNoDriverAvailableEvent(NoDriverAvailableEvent noDriverAvailableEvent) {
        Long rideId = noDriverAvailableEvent.getRideId();
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(String.valueOf(rideId)));
        if (ride.getStatus() == RideStatus.PENDING) {
            return; // Do nothing if already PENDING
        }
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Ride must be REQUESTED before pending");
        }
        ride.setStatus(RideStatus.PENDING);
        rideRepository.save(ride);
    }
}