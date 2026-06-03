package com.rakesh.expensetracker.notification.service;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

public interface NotificationService {

    void processBudgetAlert(
            ExpenseCreatedEvent event
    );
}