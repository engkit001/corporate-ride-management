package com.in6225.crms.userservice.service;

import com.in6225.crms.userservice.dto.UserDto;
import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.enums.Role;
import com.in6225.crms.userservice.exception.UserNotFoundException;
import com.in6225.crms.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.valueOf(userDto.getRole()));
        User savedUser = userRepository.save(user);
        return new UserDto(
          user.getUsername(),
          "*****",
          String.valueOf(user.getRole())
        );
    }

    public User findById(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
