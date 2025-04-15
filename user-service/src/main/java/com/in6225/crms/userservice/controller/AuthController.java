package com.in6225.crms.userservice.controller;

import com.in6225.crms.userservice.dto.AuthRequest;
import com.in6225.crms.userservice.dto.AuthResponse;
import com.in6225.crms.userservice.dto.UserDto;
import com.in6225.crms.userservice.service.AuthService;
import com.in6225.crms.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        UserDto userDto = userService.getByUsername(authRequest.getUsername());
        String token =  authService.login(userDto.getUsername(), String.valueOf(userDto.getRole()));

        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }
}
