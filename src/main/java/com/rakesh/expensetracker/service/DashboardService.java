package com.rakesh.expensetracker.service;

import com.rakesh.expensetracker.dto.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboard(Long userId);
}
