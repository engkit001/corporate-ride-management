package com.in6225.crms.driverservice.controller;

import com.in6225.crms.driverservice.dto.DriverRegistrationRequest;
import com.in6225.crms.driverservice.entity.Driver;
import com.in6225.crms.driverservice.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // Get driver by id
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    // Get drivers by status
    @GetMapping
    public ResponseEntity<List<Driver>> getDriversByStatus(@RequestParam(required = false) String status) {
        List<Driver> drivers = driverService.getDriversByStatus(status);
        return ResponseEntity.ok(drivers);
    }

    // Add new driver to the system with default AVAILABLE status
    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody DriverRegistrationRequest driverRegistrationRequest) {
        Driver savedDriver = driverService.registerDriver(driverRegistrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

}
