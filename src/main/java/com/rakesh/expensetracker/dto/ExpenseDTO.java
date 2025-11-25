package com.rakesh.expensetracker.dto;

import java.time.LocalDateTime;

public class ExpenseDTO {
    private Long id;
    private Double amount;
    private String description;
    private LocalDateTime expenseDate;
    private String categoryName;

    // Constructors
    public ExpenseDTO() {}

    public ExpenseDTO(Long id, Double amount, String description, LocalDateTime expenseDate, String categoryName) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }
    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
