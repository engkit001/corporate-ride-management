package com.in6225.crms.rideservice.service.impl;

import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.in6225.crms.rideevents.NoDriverAvailableEvent;
import com.in6225.crms.rideservice.dto.RideDto;
import com.in6225.crms.rideservice.dto.RideRequestDto;
import com.in6225.crms.rideservice.enums.RideStatus;
import com.in6225.crms.rideservice.exception.InvalidRideStateException;
import com.in6225.crms.rideservice.exception.OpenRideExistsException;
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
        return mapToDto(ride);
    }

    @Override
    public List<RideDto> getRidesByUserIdAndStatus(String userId, String status) {
        List<Ride> rideList;

        if ((userId == null || userId.isEmpty()) && (status == null || status.isEmpty())) {
            // Case 1: both are null/empty
            rideList = rideRepository.findAll();
        }
        else if (userId == null || userId.isEmpty()) {
            // Case 2: only status is provided
            rideList = rideRepository.findAllByStatus(RideStatus.valueOf(status));
        }
        else if (status == null || status.isEmpty()) {
            // Case 3: only userId is provided
            rideList = rideRepository.findAllByUserId(userId);
        }
        else {
            // Case 4: both status and userId are provided
            rideList = rideRepository.findAllByUserIdAndStatus(userId, RideStatus.valueOf(status));
        }

        List<RideDto> rideDtoList = new ArrayList<>();
        for (Ride ride : rideList) {
            RideDto rideDto = mapToDto(ride);
            rideDtoList.add(rideDto);
        }
        return rideDtoList;
    }

    @Override
    public List<RideDto> getRidesByDriverId(String driverId) {
        List<Ride> rideList = rideRepository.findAllByDriverId(driverId);
        List<RideDto> rideDtoList = new ArrayList<>();
        for (Ride ride : rideList) {
            RideDto rideDto = mapToDto(ride);
            rideDtoList.add(rideDto);
        }
        return rideDtoList;
    }

    @Override
    public RideDto requestRide(RideRequestDto rideRequestDto) {
        // Check for existing ride not COMPLETED or CANCELLED
        String userId = rideRequestDto.getUserId();
        List<RideStatus> excluded = List.of(RideStatus.COMPLETED, RideStatus.CANCELLED);  // List of excluded statuses
        List<Ride> rideList = rideRepository.findByUserIdAndStatusNotIn(userId, excluded);
        if (!rideList.isEmpty()) {
            throw new OpenRideExistsException(userId);
        }

        Ride ride = new Ride();
        ride.setUserId(rideRequestDto.getUserId());
        ride.setPickupLocation(rideRequestDto.getPickupLocation());
        ride.setDropoffLocation(rideRequestDto.getDropoffLocation());
        ride.setRideRequestedTime(LocalDateTime.now());
        ride.setStatus(RideStatus.REQUESTED);
        Ride savedRide = rideRepository.save(ride);

        // Publish RIDE_REQUESTED event to Kafka
        kafkaTemplate.send("ride-requested", savedRide.getId().toString());

        return mapToDto(savedRide);
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

        return mapToDto(savedRide);
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

        return mapToDto(savedRide);    }

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

                return mapToDto(savedRide);
            }
            // REQUESTED, ONGOING, COMPLETED, CANCELLED
            default ->
                    throw new InvalidRideStateException("Ride cannot be cancelled: " + ride.getStatus());
        }
    }

    private RideDto mapToDto(Ride ride) {
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