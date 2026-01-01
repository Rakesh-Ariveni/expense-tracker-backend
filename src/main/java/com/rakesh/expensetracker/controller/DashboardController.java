package com.rakesh.expensetracker.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(Authentication authentication) {
        System.out.println("🎯 Controller hit. Authentication = " + authentication);

        if (authentication == null) {
            throw new RuntimeException("Authentication is NULL");
        }

        String email = authentication.getName();
        System.out.println("👤 Authenticated email = " + email);

        return dashboardService.getDashboardByEmail(email);
    }
}