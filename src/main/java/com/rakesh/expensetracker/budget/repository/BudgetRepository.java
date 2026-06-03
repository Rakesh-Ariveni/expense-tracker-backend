package com.rakesh.expensetracker.budget.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.budget.entity.BudgetType;
import com.rakesh.expensetracker.entity.User;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndBudgetType(
            User user,
            BudgetType budgetType
    );
}