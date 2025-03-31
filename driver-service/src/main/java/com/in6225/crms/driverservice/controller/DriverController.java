package com.in6225.crms.driverservice.controller;

import com.in6225.crms.driverservice.model.Driver;
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

    // Get drivers by status
    @GetMapping
    public ResponseEntity<List<Driver>> getDriversByStatus(@RequestParam(required = false) String status) {
        List<Driver> drivers = driverService.getDriversByStatus(status);
        return ResponseEntity.ok(drivers);
    }

    // Add new driver to the system with default AVAILABLE status
    @PostMapping("/register")
    public ResponseEntity<Driver> registerDriver(@RequestBody Driver driver) {
        Driver savedDriver = driverService.registerDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

}
