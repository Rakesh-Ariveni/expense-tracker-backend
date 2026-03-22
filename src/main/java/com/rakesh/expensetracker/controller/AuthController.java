package com.rakesh.expensetracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rakesh.expensetracker.dto.LoginRequest;
import com.rakesh.expensetracker.dto.RegisterRequest;
import com.rakesh.expensetracker.service.AuthService;
import com.rakesh.expensetracker.service.MonitoringService;
import com.rakesh.expensetracker.service.RateLimitService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final RateLimitService rateLimitService;
    private final MonitoringService monitoringService;


    public AuthController(AuthService authService, RateLimitService rateLimitService, MonitoringService monitoringService) {
        this.authService = authService;
		this.rateLimitService = rateLimitService;
		this.monitoringService = monitoringService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        log.info("API HIT: Register email={}", request.getEmail());
        monitoringService.incrementApi("register");

        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        log.info("API HIT: Login email={}", request.getEmail());
        String email = request.getEmail();
        monitoringService.incrementApi("login");
        
        if (!rateLimitService.isAllowed(email, 3, 1)) {
            throw new RuntimeException("Too many login attempts");
        }

        return authService.login(request);
    }
}