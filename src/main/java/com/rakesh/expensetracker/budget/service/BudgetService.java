package com.rakesh.expensetracker.budget.service;

import java.util.List;

import com.rakesh.expensetracker.budget.dto.BudgetRequest;
import com.rakesh.expensetracker.budget.dto.BudgetResponse;

public interface BudgetService {

    BudgetResponse createBudget(
            String email,
            BudgetRequest request
    );

    List<BudgetResponse> getUserBudgets(
            String email
    );
}