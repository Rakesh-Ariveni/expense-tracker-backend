package com.rakesh.expensetracker.budget.entity;

import java.time.LocalDate;

import com.rakesh.expensetracker.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "budgets",
        indexes = {
                @Index(
                        name = "idx_budget_user_type",
                        columnList = "user_id,budget_type"
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================
    // USER
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // =========================================
    // BUDGET TYPE
    // =========================================

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type", nullable = false)
    private BudgetType budgetType;

    // =========================================
    // TOTAL BUDGET
    // =========================================

    @Column(name = "total_budget", nullable = false)
    private Double totalBudget;

    // =========================================
    // ALERT THRESHOLDS
    // =========================================

    @Column(name = "warning_threshold")
    private Double warningThreshold;

    @Column(name = "critical_threshold")
    private Double criticalThreshold;

    // =========================================
    // DATE RANGE
    // =========================================

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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