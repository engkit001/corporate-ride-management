package com.in6225.crms.driverservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String id; // DRIVER001, DRIVER002
    private String name;
    private String phoneNumber;
    private String status; // AVAILABLE, ASSIGNED, BUSY, OFFLINE
}
