package com.in6225.crms.driverservice.repository;

import com.in6225.crms.driverservice.entity.Driver;
import com.in6225.crms.driverservice.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {
    Optional<Driver> findFirstByStatus(DriverStatus status);
    List<Driver> findAllByStatus(DriverStatus status);
}
