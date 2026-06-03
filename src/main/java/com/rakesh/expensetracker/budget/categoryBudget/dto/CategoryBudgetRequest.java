package com.rakesh.expensetracker.budget.categoryBudget.dto;

import jakarta.validation.constraints.NotNull;

public class CategoryBudgetRequest {

    @NotNull
    private Long budgetId;

    @NotNull
    private Long categoryId;

    @NotNull
    private Double allocatedAmount;

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(Double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}