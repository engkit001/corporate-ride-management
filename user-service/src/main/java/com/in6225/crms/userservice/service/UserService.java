package com.in6225.crms.userservice.service;

import com.in6225.crms.userservice.dto.UserDto;
import com.in6225.crms.userservice.entity.User;

public interface UserService {

    UserDto saveUser(UserDto userDto);
    User findById(String username);
}
