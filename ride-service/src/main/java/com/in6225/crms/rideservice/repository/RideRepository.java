package com.in6225.crms.rideservice.repository;

import com.in6225.crms.rideservice.entity.Ride;
import com.in6225.crms.rideservice.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByUserId(String userId);

    List<Ride> findAllByStatus(RideStatus status);
}
