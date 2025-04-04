package com.in6225.crms.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 10, message = "Username must be at most 10 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(PASSENGER|DRIVER|ADMIN)$", message = "Role must be PASSENGER, DRIVER, or ADMIN")
    private String role;
}