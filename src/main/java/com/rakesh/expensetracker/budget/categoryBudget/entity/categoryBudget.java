package com.rakesh.expensetracker.budget.categoryBudget.entity;

import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.entity.Category;

import jakarta.persistence.*;

@Entity
@Table(
        name = "category_budgets",
        indexes = {
                @Index(
                        name = "idx_category_budget",
                        columnList = "budget_id,category_id"
                )
        }
)
public class categoryBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================================
    // PARENT BUDGET
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "budget_id",
            nullable = false
    )
    private Budget budget;

    // =========================================
    // CATEGORY
    // =========================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    // =========================================
    // ALLOCATED AMOUNT
    // =========================================

    @Column(
            name = "allocated_amount",
            nullable = false
    )
    private Double allocatedAmount;

    // =========================================
    // GETTERS / SETTERS
    // =========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(Double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }
}