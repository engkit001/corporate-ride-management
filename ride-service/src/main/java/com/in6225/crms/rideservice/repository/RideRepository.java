package com.in6225.crms.rideservice.repository;

import com.in6225.crms.rideservice.entity.Ride;
import com.in6225.crms.rideservice.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findAllByUserId(String userId);

    List<Ride> findAllByStatus(RideStatus status);

    List<Ride> findAllByUserIdAndStatus(String userId, RideStatus status);

    List<Ride> findByUserIdAndStatusNotIn(String userId, List<RideStatus> statuses);
}
