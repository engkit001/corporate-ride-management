package com.in6225.crms.driverservice.controller;

import com.in6225.crms.driverservice.dto.DriverDto;
import com.in6225.crms.driverservice.entity.Driver;
import com.in6225.crms.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drivers")
@AllArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable String id) {
        DriverDto driverDto = driverService.getDriverById(id);
        return new ResponseEntity<>(driverDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DriverDto>> getDriversByStatus(@RequestParam(required = false) String status) {
        List<DriverDto> driverDtoList = driverService.getDriversByStatus(status);
        return new ResponseEntity<>(driverDtoList, HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<DriverDto> registerDriver(@Valid @RequestBody DriverDto driverDto) {
        DriverDto savedDriverDto = driverService.registerDriver(driverDto);
        return new ResponseEntity<>(savedDriverDto, HttpStatus.CREATED);
    }
}
