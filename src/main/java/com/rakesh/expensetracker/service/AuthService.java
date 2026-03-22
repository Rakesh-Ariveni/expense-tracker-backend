package com.rakesh.expensetracker.service;

import com.rakesh.expensetracker.dto.LoginRequest;
import com.rakesh.expensetracker.dto.RegisterRequest;
import com.rakesh.expensetracker.dto.RegisterResponse;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.BadRequestException;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.config.JwtService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> register(RegisterRequest request) {

        log.info("Register request received for email={}", request.getEmail());

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("User registered successfully id={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        RegisterResponse response =
                new RegisterResponse(savedUser.getId(), savedUser.getEmail());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> login(LoginRequest request) {

        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed - user not found email={}", request.getEmail());
                    return new BadRequestException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Login failed - invalid password for email={}", request.getEmail());
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());

        log.info("Login successful for email={}", user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}