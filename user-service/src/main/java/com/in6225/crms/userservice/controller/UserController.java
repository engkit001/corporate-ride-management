package com.in6225.crms.userservice.controller;

import com.in6225.crms.userservice.dto.UserDto;
import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.saveUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("{username}")
    public ResponseEntity<UserDto> getByUsername(@PathVariable String username) {
        return new ResponseEntity<>(userService.getByUsername(username), HttpStatus.OK);
    }
}
