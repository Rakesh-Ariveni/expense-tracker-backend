package com.rakesh.expensetracker.service.analytics;

import java.util.Map;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

public interface AnalyticsService {

    void updateAnalytics(ExpenseCreatedEvent event);

    Double getTotalExpenses(Long userId);

    Long getExpenseCount(Long userId);

    Map<Object, Object> getCategoryAnalytics(Long userId);
    
    void clearAnalytics(Long userId);

    Double getCategoryTotal(
            Long userId,
            String category
    );
}