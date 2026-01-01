package com.rakesh.expensetracker.controller;

import com.rakesh.expensetracker.model.AuthRequest;
import com.rakesh.expensetracker.model.AuthResponse;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.config.JwtService;
import com.rakesh.expensetracker.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.rakesh.expensetracker.dto.LoginRequest;
import com.rakesh.expensetracker.dto.RegisterRequest;
import com.rakesh.expensetracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // 🔥 REQUIRED for Swagger/browser
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}