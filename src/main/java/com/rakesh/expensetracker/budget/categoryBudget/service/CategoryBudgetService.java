package com.rakesh.expensetracker.budget.categoryBudget.service;

import java.util.List;

import com.rakesh.expensetracker.budget.categoryBudget.dto.CategoryBudgetRequest;
import com.rakesh.expensetracker.budget.categoryBudget.dto.CategoryBudgetResponse;

public interface CategoryBudgetService {

    CategoryBudgetResponse createCategoryBudget(
            String email,
            CategoryBudgetRequest request
    );

    List<CategoryBudgetResponse> getBudgetCategories(
            Long budgetId
    );
}