package com.in6225.crms.userservice.controller;

import com.in6225.crms.userservice.dto.AuthRequest;
import com.in6225.crms.userservice.dto.AuthResponse;
import com.in6225.crms.userservice.entity.User;
import com.in6225.crms.userservice.security.JwtUtil;
import com.in6225.crms.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> register(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getId(), authRequest.getPassword()));

        User user = userService.findById(authRequest.getId()).orElseThrow();
        String token = jwtUtil.generateToken(user.getId(), String.valueOf(user.getRole()));

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
