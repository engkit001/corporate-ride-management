package com.in6225.crms.rideservice.service;

import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.in6225.crms.rideevents.NoDriverAvailableEvent;
import com.in6225.crms.rideservice.enums.RideStatus;
import com.in6225.crms.rideservice.exception.InvalidRideStateException;
import com.in6225.crms.rideservice.exception.RideNotFoundException;
import com.in6225.crms.rideservice.entity.Ride;
import com.in6225.crms.rideservice.repository.RideRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final KafkaTemplate<Object, String> kafkaTemplate;

    public RideService(RideRepository rideRepository, KafkaTemplate<Object, String> kafkaTemplate) {
        this.rideRepository = rideRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Ride getRideById(Long id) {
        Optional<Ride> rideOptional = rideRepository.findById(id);

        if (rideOptional.isEmpty()) {
            throw new RideNotFoundException(id);
        }

        return rideOptional.get();
    }

    public List<Ride> getRidesByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return rideRepository.findAll(); // Return all drivers if no status is provided
        }
        return rideRepository.findByUserId(userId);
    }

    public Ride requestRide(Ride ride) {
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRideRequestedTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        // Publish RIDE_REQUESTED event to Kafka
        kafkaTemplate.send("ride-requested", savedRide.getId().toString());

        return savedRide;
    }

    public Ride startRide(Long id) {
        Ride ride = this.getRideById(id);
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStateException("Ride must be ASSIGNED before starting.");
        }
        ride.setStatus(RideStatus.ONGOING);
        ride.setRideStartTime(LocalDateTime.now());
        rideRepository.save(ride);

        // Publish RIDE_STARTED event to Kafka
        kafkaTemplate.send("ride-started", String.valueOf(ride.getId()) + ":" + ride.getDriverId());

        return ride;
    }

    public Ride completeRide(Long id) {
        Ride ride = this.getRideById(id);
        if (ride.getStatus() != RideStatus.ONGOING) {
            throw new InvalidRideStateException("Ride must be ONGOING before completing.");
        }
        ride.setRideEndTime(LocalDateTime.now());
        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);

        // Publish RIDE_COMPLETED event to Kafka
        kafkaTemplate.send("ride-completed", String.valueOf(ride.getId()) + ":" + ride.getDriverId());

        return ride;
    }

    public Ride cancelRide(Long id) {
        Ride ride = this.getRideById(id);
        switch (ride.getStatus()) {
            case REQUESTED, PENDING, ASSIGNED -> {
                ride.setRideCanceledTime(LocalDateTime.now());
                ride.setStatus(RideStatus.CANCELLED);
                rideRepository.save(ride);

                // Publish RIDE_STARTED event to Kafka
                kafkaTemplate.send("ride-cancelled", String.valueOf(ride.getId()) + ":" + ride.getDriverId());

                return ride;
            }
            default -> { // ONGOING, COMPLETED, CANCELLED
                throw new InvalidRideStateException("Ride cannot be cancelled: " + ride.getStatus());
            }
        }
    }

    public void handleDriverAssignedEvent(DriverAssignedEvent driverAssignedEvent) {
        Ride ride = this.getRideById(driverAssignedEvent.getRideId());
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Ride must be REQUESTED before assigning");
        }
        ride.setDriverId(driverAssignedEvent.getDriverId());
        ride.setRideAssignedTime(LocalDateTime.now());
        ride.setStatus(RideStatus.ASSIGNED);
        rideRepository.save(ride);
    }

    public void handleNoDriverAvailableEvent(NoDriverAvailableEvent noDriverAvailableEvent) {
        Ride ride = this.getRideById(noDriverAvailableEvent.getRideId());
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Ride must be REQUESTED before pending");
        }
        ride.setStatus(RideStatus.PENDING);
        rideRepository.save(ride);
    }

}

