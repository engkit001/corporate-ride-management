package com.in6225.crms.rideservice.repository;

import com.in6225.crms.rideservice.entity.Ride;
import com.in6225.crms.rideservice.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findAllByStatus(RideStatus status);

    List<Ride> findByUserIdAndStatusNotIn(String userId, List<RideStatus> statuses);

    @Query("SELECT r FROM Ride r " +
            "WHERE (:userId IS NULL OR r.userId = :userId) " +
            "AND (:driverId IS NULL OR r.driverId = :driverId) " +
            "AND (:status IS NULL OR r.status = :status)")
    List<Ride> findByOptionalFilters(@Param("userId") String userId,
                                     @Param("driverId") String driverId,
                                     @Param("status") RideStatus status);
}
