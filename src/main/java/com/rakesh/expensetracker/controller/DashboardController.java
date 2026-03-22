package com.rakesh.expensetracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.service.DashboardService;
import com.rakesh.expensetracker.service.RateLimitService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;
    private final RateLimitService rateLimitService;

    
    public DashboardController(DashboardService dashboardService, RateLimitService rateLimitService) {
        this.dashboardService = dashboardService;
		this.rateLimitService = rateLimitService;
    }

    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication) {

        log.info("API HIT: Get Dashboard");

        if (authentication == null) {
            log.error("Authentication is NULL in dashboard request");
            throw new ResourceNotFoundException("Authentication is NULL");
        }

        String email = authentication.getName();
        
        if (!rateLimitService.isAllowed(email, 20, 2)) {
            throw new RuntimeException("Too many requests");
        }

        log.debug("Authenticated user email={}", email);

        DashboardResponse response = dashboardService.getDashboardByEmail(email);

        log.info("Dashboard fetched successfully for email={}", email);

        return response;
    }
}