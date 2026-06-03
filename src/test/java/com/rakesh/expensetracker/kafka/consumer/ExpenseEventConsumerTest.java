package com.rakesh.expensetracker.kafka.consumer;

import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;
import com.rakesh.expensetracker.service.analytics.AnalyticsService;

@ExtendWith(MockitoExtension.class)
class ExpenseEventConsumerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private ExpenseEventConsumer consumer;

    @Test
    void testConsumeExpenseCreatedEvent() {

        ExpenseCreatedEvent event =
                new ExpenseCreatedEvent(
                        1L,
                        1L,
                        500.0,
                        "Food",
                        LocalDateTime.now()
                );

        consumer.consumeExpenseCreatedEvent(event);

        verify(analyticsService)
                .updateAnalytics(event);
    }
}