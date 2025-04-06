package com.in6225.crms.userservice.service;

import com.in6225.crms.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto userDto);
    UserDto getByUsername(String username);
    List<UserDto> getAll();
}
