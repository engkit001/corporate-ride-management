package com.in6225.crms.driverservice.entity;

import com.in6225.crms.driverservice.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    @Id
    @Column(unique = true)
    private String id;
    private String name;
    private String phoneNumber;
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;
}
