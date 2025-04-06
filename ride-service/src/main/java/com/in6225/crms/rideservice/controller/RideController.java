package com.in6225.crms.rideservice.controller;

import com.in6225.crms.rideservice.dto.RideDto;
import com.in6225.crms.rideservice.dto.RideRequestDto;
import com.in6225.crms.rideservice.service.RideService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rides")
@AllArgsConstructor
public class RideController {
    private final RideService rideService;

    @GetMapping("{id}")
    public ResponseEntity<RideDto> getRideById(@PathVariable Long id) {
        return new ResponseEntity<>(rideService.getRideById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RideDto>> getRidesByUserIdAndStatus(@RequestParam(required = false) String userId, String status) {
        return new ResponseEntity<>(rideService.getRidesByUserIdAndStatus(userId, status), HttpStatus.OK);
    }

    @GetMapping("by-driver")
    public ResponseEntity<List<RideDto>> getRidesByDriverId(@RequestParam String driverId) {
        return new ResponseEntity<>(rideService.getRidesByDriverId(driverId), HttpStatus.OK);
    }

    @PostMapping("request")
    public ResponseEntity<RideDto> requestRide(@Valid @RequestBody RideRequestDto rideRequestDto) {
        return new ResponseEntity<>(rideService.requestRide(rideRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("{id}/start")
    public ResponseEntity<RideDto> startRide(@PathVariable Long id) {
        return new ResponseEntity<>(rideService.startRide(id), HttpStatus.OK);
    }

    @PatchMapping("{id}/complete")
    public ResponseEntity<RideDto> completeRide(@PathVariable Long id) {
        return new ResponseEntity<>(rideService.completeRide(id), HttpStatus.OK);
    }

    @PatchMapping("{id}/cancel")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long id) {
        return new ResponseEntity<>(rideService.cancelRide(id), HttpStatus.OK);
    }
}

