package com.rakesh.expensetracker.service.impl;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardResponse getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expenses = expenseRepository.findByUser(user);

        DashboardResponse response = new DashboardResponse();
        response.setUserId(userId);

        if (expenses.isEmpty()) return response;

        // 1️⃣ Total Expenses
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        response.setTotalExpenses(total);

        // 2️⃣ Expenses by Category
        Map<String, Double> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getName(),
                        Collectors.summingDouble(Expense::getAmount)
                ));
        response.setExpensesByCategory(byCategory);

        // 3️⃣ Top Spending Category — Using PriorityQueue
        PriorityQueue<Map.Entry<String, Double>> pq =
                new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));
        pq.addAll(byCategory.entrySet());
        response.setTopCategory(pq.peek().getKey());

        // 4️⃣ Weekly Spending Trend
        Map<String, Double> weeklyTrend = new TreeMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            double sum = expenses.stream()
                    .filter(e -> e.getExpenseDate().toLocalDate().equals(date))
                    .mapToDouble(Expense::getAmount)
                    .sum();
            weeklyTrend.put(date.toString(), sum);
        }
        response.setWeeklyTrends(weeklyTrend);

        // 5️⃣ Most Frequent Category
        String frequent = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getCategory().getName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        response.setMostFrequentCategory(frequent);

        // 6️⃣ Max Spending Streak — DP (Kadane’s)
        List<Double> amounts = expenses.stream()
                .sorted(Comparator.comparing(Expense::getExpenseDate))
                .map(Expense::getAmount)
                .collect(Collectors.toList());

        double maxStreak = amounts.get(0), current = amounts.get(0);
        for (int i = 1; i < amounts.size(); i++) {
            current = Math.max(amounts.get(i), current + amounts.get(i));
            maxStreak = Math.max(maxStreak, current);
        }
        response.setMaxSpendingStreak(maxStreak);

        // 7️⃣ Recent Expenses
        List<Expense> recent = expenses.stream()
                .sorted(Comparator.comparing(Expense::getExpenseDate).reversed())
                .limit(5)
                .collect(Collectors.toList());
        response.setRecentExpenses(mapToExpenseDTOList(recent));

        return response;
    }

    private List<ExpenseDTO> mapToExpenseDTOList(List<Expense> expenses) {
        return expenses.stream()
                .map(e -> new ExpenseDTO(
                        e.getId(),
                        e.getAmount(),
                        e.getDescription(),
                        e.getExpenseDate(),
                        e.getCategory().getName()
                ))
                .collect(Collectors.toList());
    }
}
