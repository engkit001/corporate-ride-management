package com.in6225.crms.userservice.service;

import com.in6225.crms.userservice.dto.RegistrationRequest;
import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.enums.Role;
import com.in6225.crms.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.valueOf(registrationRequest.getRole()));
        return userRepository.save(user);
    }

    public Optional<User> findById(String username) {
        return userRepository.findByUsername(username);
    }
}
