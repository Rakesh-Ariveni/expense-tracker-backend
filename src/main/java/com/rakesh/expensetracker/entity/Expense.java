package com.rakesh.expensetracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "expenses",
    indexes = {

        // 🔥 Pagination + sorting
        @Index(name = "idx_user_date", columnList = "user_id, expense_date"),

        // 🔥 Category filtering
        @Index(name = "idx_user_category", columnList = "user_id, category_id"),

        // 🔥 Dashboard aggregation
        @Index(name = "idx_category", columnList = "category_id"),

        // 🔥 Sorting optimization
        @Index(name = "idx_created_at", columnList = "created_at"),

        // 🔥 Time-series optimization (advanced)
        @Index(name = "idx_user_expense_day", columnList = "user_id, expense_day")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String description;

    private LocalDateTime expenseDate;

    // 🔥 NEW FIELD (important)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 🔥 OPTIONAL BUT POWERFUL
    @Column(name = "expense_day")
    private LocalDate expenseDay;

    // Many expenses belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Many expenses belong to one category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 🔥 Auto-set fields
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.expenseDate != null) {
            this.expenseDay = this.expenseDate.toLocalDate();
        }
    }
    
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
