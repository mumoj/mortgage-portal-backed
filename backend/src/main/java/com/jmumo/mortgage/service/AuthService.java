package com.jmumo.mortgage.service;


import com.jmumo.mortgage.model.dto.AuthRequest;
import com.jmumo.mortgage.model.dto.AuthResponse;
import com.jmumo.mortgage.model.entity.User;
import com.jmumo.mortgage.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.isEnabled()) {
            throw new BadCredentialsException("User account is disabled");
        }

        String token = generateToken(user);
        return AuthResponse.of(token, user.getUsername(), user.getRole().name(), jwtExpiration);
    }

    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtExpiration);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }
}