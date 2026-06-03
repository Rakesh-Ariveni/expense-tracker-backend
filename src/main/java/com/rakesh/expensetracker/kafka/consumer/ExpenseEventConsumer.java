package com.rakesh.expensetracker.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;
import com.rakesh.expensetracker.service.analytics.AnalyticsService;

@Service
public class ExpenseEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(ExpenseEventConsumer.class);

    private final AnalyticsService analyticsService;

    public ExpenseEventConsumer(
            AnalyticsService analyticsService
    ) {
        this.analyticsService = analyticsService;
    }

    @KafkaListener(
            topics = "expense-created-topic",
            groupId = "analytics-group"
    )
    public void consumeExpenseCreatedEvent(
            ExpenseCreatedEvent event
    ) {

        log.info(
                "Received expense event for analytics processing. expenseId={}",
                event.getExpenseId()
        );

        analyticsService.updateAnalytics(event);

        log.info(
                "Analytics processing completed for expenseId={}",
                event.getExpenseId()
        );
    }
}