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
    @Column(unique = true)  // Ensures 'id' is unique in the database
    private String id; // DRIVER001, DRIVER002
    private String name;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;
}
