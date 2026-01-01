package com.rakesh.expensetracker.service.impl;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.DashboardService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(ExpenseRepository expenseRepository,
                                UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "dashboard", key = "#email")
    public DashboardResponse getDashboardByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardResponse response = new DashboardResponse();
        response.setUserId(user.getId());

        // 1️⃣ Total
        response.setTotalExpenses(
                expenseRepository.findTotalExpensesByUser(user)
        );

        // 2️⃣ Category aggregation
        Map<String, Double> byCategory = new HashMap<>();
        for (Object[] row : expenseRepository.findExpensesByCategory(user)) {
            byCategory.put((String) row[0], (Double) row[1]);
        }
        response.setExpensesByCategory(byCategory);

     // 3️⃣ Top category
        String topCategory =
                expenseRepository
                        .findTopCategory(user, PageRequest.of(0, 1))
                        .stream()
                        .findFirst()
                        .orElse(null);

        response.setTopCategory(topCategory);


        // 4️⃣ Weekly trend
        Map<String, Double> weeklyTrend = new LinkedHashMap<>();
        LocalDateTime startDate =
                LocalDate.now().minusDays(6).atStartOfDay();

        for (Object[] row : expenseRepository.findWeeklyTrend(user, startDate)) {
            weeklyTrend.put(row[0].toString(), (Double) row[1]);
        }
        response.setWeeklyTrends(weeklyTrend);

        // 5️⃣ Most frequent category
        String mostFrequentCategory =
                expenseRepository
                        .findMostFrequentCategory(user, PageRequest.of(0, 1))
                        .stream()
                        .findFirst()
                        .orElse(null);

        response.setMostFrequentCategory(mostFrequentCategory);

        // 6️⃣ Max spending streak (JVM logic is correct)
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
        response.setRecentExpenses(
                mapToDTO(
                        expenseRepository.findRecentExpenses(user, PageRequest.of(0, 5))
                )
        );

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
