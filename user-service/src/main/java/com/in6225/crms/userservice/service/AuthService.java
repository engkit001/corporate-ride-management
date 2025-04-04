package com.in6225.crms.userservice.service;


import com.in6225.crms.common.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String role) {
        return jwtUtil.generateToken(username, role);
    }
}
