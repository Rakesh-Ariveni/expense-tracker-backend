package com.rakesh.expensetracker.kafka.event;

import java.time.LocalDateTime;

public class ExpenseCreatedEvent {

    private Long expenseId;
    private Long userId;
    private Double amount;
    private String category;
    private LocalDateTime createdAt;

    public ExpenseCreatedEvent() {
    }

    public ExpenseCreatedEvent(
            Long expenseId,
            Long userId,
            Double amount,
            String category,
            LocalDateTime createdAt
    ) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.createdAt = createdAt;
    }

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
