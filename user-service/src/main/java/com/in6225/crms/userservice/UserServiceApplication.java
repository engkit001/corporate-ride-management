package com.in6225.crms.userservice;

import com.in6225.crms.userservice.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.repository.UserRepository;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            if (!userRepository.existsByUsername("ADMIN")) {
                User admin = new User();
                admin.setUsername("ADMIN");
                admin.setPassword("$2a$10$mFu5OuKkS9VzRw1QlaZQ.eXY12VZXwjdm70ROvA/eJH8J.hKtIHPG"); // BCrypted
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
                System.out.println("Admin user created.");
            } else {
                System.out.println("Admin user already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
