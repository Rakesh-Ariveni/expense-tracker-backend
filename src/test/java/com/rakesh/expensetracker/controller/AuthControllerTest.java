package com.rakesh.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakesh.expensetracker.config.JwtAuthenticationFilter;
import com.rakesh.expensetracker.config.JwtService;
import com.rakesh.expensetracker.dto.LoginRequest;
import com.rakesh.expensetracker.dto.RegisterRequest;
import com.rakesh.expensetracker.dto.RegisterResponse;
import com.rakesh.expensetracker.service.AuthService;
import com.rakesh.expensetracker.service.MonitoringService;
import com.rakesh.expensetracker.service.RateLimitService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RateLimitService rateLimitService;

    @MockBean
    private MonitoringService monitoringService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private Authentication authentication;

    @Test
    void testRegisterSuccess() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setName("Rakesh");
        request.setEmail("test@gmail.com");
        request.setPassword("password");

        RegisterResponse response =
                new RegisterResponse(
                        1L,
                        "test@gmail.com"
                );

        when(authService.register(any(RegisterRequest.class)))
        .thenAnswer(invocation ->
            ResponseEntity.ok(
                new RegisterResponse(
                    1L,
                    "test@gmail.com"
                )
            )
        );

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void testLoginSuccess() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password");

        Map<String, String> token =
                new HashMap<>();

        token.put("token", "jwt-token");

        when(rateLimitService.isAllowed(
                any(),
                any(Integer.class),
                any(Integer.class)
        )).thenReturn(true);

        when(authService.login(any(LoginRequest.class)))
        .thenAnswer(invocation ->
            ResponseEntity.ok(
                new RegisterResponse(
                    1L,
                    "test@gmail.com"
                )
            )
        );

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void testLoginRateLimitExceeded() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password");

        when(rateLimitService.isAllowed(
                any(),
                any(Integer.class),
                any(Integer.class)
        )).thenReturn(false);

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(request)
                        )
        )
        .andExpect(status().is5xxServerError());
    }
}