package com.rakesh.expensetracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);

    @Async
    public void logExpenseCreation(Long expenseId) {
        log.info("Async processing started for expenseId={}", expenseId);

        try {
            Thread.sleep(3000); // simulate heavy work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Async processing completed for expenseId={}", expenseId);
    }
}