package com.in6225.crms.userservice.service;

import com.in6225.crms.common.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    public String login(String username, String role) {
        return jwtUtil.generateToken(username, role);
    }
}
