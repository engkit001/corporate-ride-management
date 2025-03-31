package com.in6225.crms.rideservice.service;

import com.in6225.crms.rideevents.DriverAssignedEvent;
import com.in6225.crms.rideevents.NoDriverAvailableEvent;
import com.in6225.crms.rideservice.exception.RideNotFoundException;
import com.in6225.crms.rideservice.model.Ride;
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
        // Find the ride
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
        ride.setStatus("REQUESTED");
        ride.setRideRequestedTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        // Publish RIDE_REQUESTED event to Kafka
        kafkaTemplate.send("ride-requested", savedRide.getId().toString());

        return savedRide;
    }

    public Ride startRide(Long id) {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isPresent()) {
            Ride ride = rideOptional.get();
            ride.setStatus("ONGOING");
            ride.setRideStartTime(LocalDateTime.now());
            rideRepository.save(ride);

            // Publish RIDE_STARTED event to Kafka
            kafkaTemplate.send("ride-started", String.valueOf(ride.getId()) + ":" + ride.getDriverId());

            return ride;
        }
        throw new RideNotFoundException(id);
    }

    public Ride completeRide(Long id) {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isPresent()) {
            Ride ride = rideOptional.get();
            ride.setRideEndTime(LocalDateTime.now());
            ride.setStatus("COMPLETED");
            rideRepository.save(ride);

            // Publish RIDE_COMPLETED event to Kafka
            kafkaTemplate.send("ride-completed", String.valueOf(ride.getId()) + ":" + ride.getDriverId());

            return ride;
        }
        throw new RideNotFoundException(id);
    }

    public Ride cancelRide(Long id) {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isPresent()) {
            Ride ride = rideOptional.get();
            ride.setRideCanceledTime(LocalDateTime.now());
            ride.setStatus("CANCELLED");
            rideRepository.save(ride);

            return ride;
        }
        throw new RideNotFoundException(id);
    }

    public void handleDriverAssignedEvent(DriverAssignedEvent driverAssignedEvent) {
        Ride ride = rideRepository.findById(driverAssignedEvent.getRideId()).orElseThrow();
        ride.setDriverId(driverAssignedEvent.getDriverId());
        ride.setRideAssignedTime(LocalDateTime.now());
        ride.setStatus("ASSIGNED");
        rideRepository.save(ride);
    }

    public void handleNoDriverAvailableEvent(NoDriverAvailableEvent noDriverAvailableEvent) {
        Ride ride = rideRepository.findById(noDriverAvailableEvent.getRideId()).orElseThrow();
        ride.setStatus("PENDING");
        rideRepository.save(ride);
    }

}

