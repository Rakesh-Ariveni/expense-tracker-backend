package com.rakesh.expensetracker.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ExpenseRequest {
    @NotNull
    private Double amount;

    private String description;

    @NotNull
    private LocalDateTime expenseDate;

    @NotNull
    private Long userId;

    @NotNull
    private Long categoryId;
    
    private String categoryName;


    public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	// Getters & Setters
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
