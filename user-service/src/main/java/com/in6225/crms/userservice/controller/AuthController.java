package com.in6225.crms.userservice.controller;

import com.in6225.crms.userservice.dto.AuthRequest;
import com.in6225.crms.userservice.dto.AuthResponse;
import com.in6225.crms.userservice.dto.RegistrationRequest;
import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.security.JwtUtil;
import com.in6225.crms.userservice.service.UserService;
import jakarta.validation.Valid;
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
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        User user = userService.findById(authRequest.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername(), String.valueOf(user.getRole()));

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
