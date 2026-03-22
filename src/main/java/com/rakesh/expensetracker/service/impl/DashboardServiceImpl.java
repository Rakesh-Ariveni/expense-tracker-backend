package com.rakesh.expensetracker.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.DashboardService;
import com.rakesh.expensetracker.service.MonitoringService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final MonitoringService monitoringService;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            MonitoringService monitoringService
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.monitoringService = monitoringService;
    }

    @Override
    @Cacheable(value = "dashboard", key = "#email")
    public DashboardResponse getDashboardByEmail(String email) {
    	
    	monitoringService.incrementApi("dashboard");
        log.info("Fetching dashboard for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for dashboard email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        DashboardResponse response = new DashboardResponse();
        response.setUserId(user.getId());

        // 1️⃣ Total
        log.debug("Calculating total expenses for userId={}", user.getId());
        response.setTotalExpenses(
                expenseRepository.findTotalExpensesByUser(user)
        );

        // 2️⃣ Category aggregation
        log.debug("Fetching category-wise aggregation for userId={}", user.getId());
        Map<String, Double> byCategory = new HashMap<>();
        for (Object[] row : expenseRepository.findExpensesByCategory(user)) {
            byCategory.put((String) row[0], (Double) row[1]);
        }
        response.setExpensesByCategory(byCategory);

        // 3️⃣ Top category
        log.debug("Fetching top category for userId={}", user.getId());
        String topCategory =
                expenseRepository
                        .findTopCategory(user, PageRequest.of(0, 1))
                        .stream()
                        .findFirst()
                        .orElse(null);

        response.setTopCategory(topCategory);

        // 4️⃣ Weekly trend
        log.debug("Calculating weekly trends for userId={}", user.getId());
        Map<String, Double> weeklyTrend = new LinkedHashMap<>();
        LocalDate startDate = LocalDate.now().minusDays(6);

        for (Object[] row : expenseRepository.findWeeklyTrend(user, startDate)) {
            weeklyTrend.put(row[0].toString(), (Double) row[1]);
        }
        response.setWeeklyTrends(weeklyTrend);

        // 5️⃣ Most frequent category
        log.debug("Fetching most frequent category for userId={}", user.getId());
        String mostFrequentCategory =
                expenseRepository
                        .findMostFrequentCategory(user, PageRequest.of(0, 1))
                        .stream()
                        .findFirst()
                        .orElse(null);

        response.setMostFrequentCategory(mostFrequentCategory);

        // 6️⃣ Max spending streak
        log.debug("Calculating max spending streak for userId={}", user.getId());
        List<Double> amounts = expenseRepository
                .findRecentExpenses(user, PageRequest.of(0, 100))
                .stream()
                .map(Expense::getAmount)
                .toList();

        double max = 0, current = 0;
        for (double amt : amounts) {
            current = Math.max(amt, current + amt);
            max = Math.max(max, current);
        }
        response.setMaxSpendingStreak(max);

        // 7️⃣ Recent expenses
        log.debug("Fetching recent expenses for userId={}", user.getId());
        response.setRecentExpenses(
                mapToDTO(
                        expenseRepository.findRecentExpenses(user, PageRequest.of(0, 5))
                )
        );

        log.info("Dashboard fetched successfully for userId={}", user.getId());

        return response;
    }

    private List<ExpenseDTO> mapToDTO(List<Expense> expenses) {
        return expenses.stream()
                .map(e -> new ExpenseDTO(
                        e.getId(),
                        e.getAmount(),
                        e.getDescription(),
                        e.getExpenseDate(),
                        e.getCategory().getName()
                ))
                .toList();
    }
}