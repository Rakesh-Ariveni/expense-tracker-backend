package com.rakesh.expensetracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rakesh.expensetracker.budget.categoryBudget.repository.CategoryBudgetRepository;
import com.rakesh.expensetracker.budget.repository.BudgetRepository;
import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.MonitoringService;
import com.rakesh.expensetracker.service.analytics.AnalyticsService;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MonitoringService monitoringService;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryBudgetRepository categoryBudgetRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void testGetDashboard_UserNotFound() {

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.getDashboardByEmail(
                        "test@gmail.com"
                )
        );
    }
    
    @Test
    void testGetDashboard_CacheHit() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(analyticsService.getTotalExpenses(
                1L))
                .thenReturn(5000.0);

        when(budgetRepository.findByUser(user))
                .thenReturn(List.of());

        when(analyticsService.getCategoryAnalytics(
                1L))
                .thenReturn(Map.of());

        when(expenseRepository.findWeeklyTrend(
                any(),
                any()))
                .thenReturn(List.of());

        when(expenseRepository.findMostFrequentCategory(
                any(),
                any()))
                .thenReturn(List.of());

        when(expenseRepository.findRecentExpenses(
                any(),
                any()))
                .thenReturn(List.of());

        DashboardResponse response =
                dashboardService.getDashboardByEmail(
                        "test@gmail.com"
                );

        assertEquals(
                5000.0,
                response.getTotalExpenses()
        );

        verify(monitoringService)
                .cacheHit();
    }
    
    @Test
    void testGetDashboard_CacheMiss() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.of(user));

        // Redis miss
        when(analyticsService.getTotalExpenses(
                1L))
                .thenReturn(0.0);

        // MySQL fallback
        when(expenseRepository.findTotalExpensesByUser(
                user))
                .thenReturn(3500.0);

        when(budgetRepository.findByUser(user))
                .thenReturn(List.of());

        when(analyticsService.getCategoryAnalytics(
                1L))
                .thenReturn(Map.of());

        when(expenseRepository.findWeeklyTrend(
                any(),
                any()))
                .thenReturn(List.of());

        when(expenseRepository.findMostFrequentCategory(
                any(),
                any()))
                .thenReturn(List.of());

        when(expenseRepository.findRecentExpenses(
                any(),
                any()))
                .thenReturn(List.of());

        DashboardResponse response =
                dashboardService.getDashboardByEmail(
                        "test@gmail.com"
                );

        assertEquals(
                3500.0,
                response.getTotalExpenses()
        );

        verify(monitoringService, times(2))
        .cacheMiss();

        verify(expenseRepository)
                .findTotalExpensesByUser(user);
    }
}