package com.in6225.crms.userservice.service.impl;

import com.in6225.crms.userservice.dto.UserDto;
import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.enums.Role;
import com.in6225.crms.userservice.exception.UserNotFoundException;
import com.in6225.crms.userservice.repository.UserRepository;
import com.in6225.crms.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.valueOf(userDto.getRole()));
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return mapToDto(user);
    }

    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            userDtoList.add(mapToDto(user));
        }
        return userDtoList;
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getPassword(),
                String.valueOf(user.getRole())
        );
    }
}
