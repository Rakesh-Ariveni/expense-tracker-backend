package com.rakesh.expensetracker.service.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rakesh.expensetracker.config.JwtService;
import com.rakesh.expensetracker.dto.LoginRequest;
import com.rakesh.expensetracker.dto.RegisterRequest;
import com.rakesh.expensetracker.dto.RegisterResponse;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.BadRequestException;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterSuccess() {

        RegisterRequest request =
                new RegisterRequest();

        request.setName("Rakesh");
        request.setEmail("test@gmail.com");
        request.setPassword("password");

        User savedUser =
                new User();

        savedUser.setId(1L);
        savedUser.setName("Rakesh");
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("encodedPassword");

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        ResponseEntity<?> response =
                authService.register(request);

        RegisterResponse registerResponse =
                (RegisterResponse) response.getBody();

        assertNotNull(registerResponse);

        assertEquals(
                1L,
                registerResponse.getUserId()
        );

        assertEquals(
                "test@gmail.com",
                registerResponse.getEmail()
        );

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void testLoginSuccess() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("test@gmail.com");
        request.setPassword("password");

        User user =
                new User();

        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password",
                "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(
                "test@gmail.com"))
                .thenReturn("jwt-token");

        ResponseEntity<?> response =
                authService.login(request);

        Map<?, ?> body =
                (Map<?, ?>) response.getBody();

        assertEquals(
                "jwt-token",
                body.get("token")
        );

        verify(jwtService)
                .generateToken("test@gmail.com");
    }

    @Test
    void testLoginUserNotFound() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("test@gmail.com");
        request.setPassword("password");

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadRequestException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void testLoginInvalidPassword() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("test@gmail.com");
        request.setPassword("wrong-password");

        User user =
                new User();

        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> authService.login(request)
        );
    }
}