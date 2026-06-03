package com.rakesh.expensetracker.service.analytics;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;

@Service
public class AnalyticsInitializationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AnalyticsInitializationService.class
            );

    private final UserRepository userRepository;

    private final ExpenseRepository expenseRepository;

    private final AnalyticsService analyticsService;

    public AnalyticsInitializationService(
            UserRepository userRepository,
            ExpenseRepository expenseRepository,
            AnalyticsService analyticsService
    ) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.analyticsService = analyticsService;
    }

    @PostConstruct
    public void initializeAnalytics() {

        log.info(
                "Starting analytics backfill process..."
        );

        List<User> users =
                userRepository.findAll();

        int totalExpensesProcessed = 0;

        for (User user : users) {

            analyticsService.clearAnalytics(
                    user.getId()
            );

            List<Expense> expenses =
                    expenseRepository.findByUser(user);

            for (Expense expense : expenses) {

                ExpenseCreatedEvent event =
                        new ExpenseCreatedEvent(
                                expense.getId(),
                                user.getId(),
                                expense.getAmount(),
                                expense.getCategory()
                                        .getName(),
                                LocalDateTime.now()
                        );

                analyticsService.updateAnalytics(
                        event
                );

                totalExpensesProcessed++;
            }

            log.info(
                    "Analytics rebuilt for userId={}, expenses={}",
                    user.getId(),
                    expenses.size()
            );
        }

        log.info(
                "Analytics backfill completed. Total expenses processed={}",
                totalExpensesProcessed
        );
    }
}