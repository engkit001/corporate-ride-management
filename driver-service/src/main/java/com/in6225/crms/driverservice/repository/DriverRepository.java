package com.in6225.crms.driverservice.repository;

import com.in6225.crms.driverservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {
    Optional<Driver> findFirstByStatus(String status);

    List<Driver> findAllByStatus(String status);
}
