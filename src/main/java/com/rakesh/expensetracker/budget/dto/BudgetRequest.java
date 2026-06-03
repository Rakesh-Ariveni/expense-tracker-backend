package com.rakesh.expensetracker.budget.dto;

import java.time.LocalDate;

import com.rakesh.expensetracker.budget.entity.BudgetType;

import jakarta.validation.constraints.NotNull;

public class BudgetRequest {

    @NotNull
    private BudgetType budgetType;

    @NotNull
    private Double totalBudget;

    // OPTIONAL
    // Default later if null
    private Double warningThreshold;

    // OPTIONAL
    // Default later if null
    private Double criticalThreshold;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Double getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(Double warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public Double getCriticalThreshold() {
        return criticalThreshold;
    }

    public void setCriticalThreshold(Double criticalThreshold) {
        this.criticalThreshold = criticalThreshold;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}