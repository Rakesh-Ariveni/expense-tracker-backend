package com.rakesh.expensetracker.notification.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;
import com.rakesh.expensetracker.notification.service.NotificationService;

@Component
public class NotificationConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    NotificationConsumer.class
            );

    private final NotificationService notificationService;

    public NotificationConsumer(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "expense-created-topic",
            groupId = "notification-group"
    )
    public void consumeExpenseEvent(
            ExpenseCreatedEvent event
    ) {

        log.info(
                "Notification consumer received event for expenseId={}",
                event.getExpenseId()
        );

        notificationService
                .processBudgetAlert(event);
    }
}