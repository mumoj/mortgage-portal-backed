package com.jmumo.mortgage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type;
    private String username;
    private String role;
    private long expiresIn;

    public static AuthResponse of(String token, String username, String role, long expiresIn) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(username)
                .role(role)
                .expiresIn(expiresIn)
                .build();
    }
}
