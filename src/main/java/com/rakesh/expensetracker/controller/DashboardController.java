package com.rakesh.expensetracker.controller;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public DashboardResponse getDashboard(@PathVariable Long userId) {
        return dashboardService.getDashboard(userId);
    }
}
