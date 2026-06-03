
package com.rakesh.expensetracker.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakesh.expensetracker.config.JwtAuthenticationFilter;
import com.rakesh.expensetracker.config.JwtService;
import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.service.DashboardService;
import com.rakesh.expensetracker.service.MonitoringService;
import com.rakesh.expensetracker.service.RateLimitService;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private RateLimitService rateLimitService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private MonitoringService monitoringService;

    @MockBean
    private Authentication authentication;

    @Test
    void testGetDashboardSuccess() throws Exception {

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(rateLimitService.isAllowed(
                anyString(),
                anyInt(),
                anyInt()))
                .thenReturn(true);

        DashboardResponse response =
                new DashboardResponse();

        when(dashboardService.getDashboardByEmail(
                anyString()))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/dashboard")
                        .principal(authentication)
        )
        .andExpect(status().isOk());
    }

    @Test
    void testGetDashboardRateLimitExceeded() throws Exception {

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(rateLimitService.isAllowed(
                anyString(),
                anyInt(),
                anyInt()))
                .thenReturn(false);

        mockMvc.perform(
                get("/api/dashboard")
                        .principal(authentication)
        )
        .andExpect(status().is5xxServerError());
    }
}