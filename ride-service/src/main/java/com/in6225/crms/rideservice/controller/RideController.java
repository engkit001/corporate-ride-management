package com.in6225.crms.rideservice.controller;

import com.in6225.crms.rideservice.dto.RideRequest;
import com.in6225.crms.rideservice.entity.Ride;
import com.in6225.crms.rideservice.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rides")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @GetMapping
    public ResponseEntity<List<Ride>> getRidesByUserId(@RequestParam(required = false) String userId) {
        return ResponseEntity.ok(rideService.getRidesByUserId(userId));
    }

    @PostMapping("/request")
    public ResponseEntity<Ride> requestRide(@RequestBody RideRequest rideRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rideService.requestRide(rideRequest));
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<Ride> startRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.completeRide(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.cancelRide(id));
    }
}

