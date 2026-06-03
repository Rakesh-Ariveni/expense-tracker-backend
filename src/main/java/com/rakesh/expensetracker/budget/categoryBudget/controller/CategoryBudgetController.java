package com.rakesh.expensetracker.budget.categoryBudget.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.rakesh.expensetracker.budget.categoryBudget.dto.CategoryBudgetRequest;
import com.rakesh.expensetracker.budget.categoryBudget.dto.CategoryBudgetResponse;
import com.rakesh.expensetracker.budget.categoryBudget.service.CategoryBudgetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/category-budgets")
public class CategoryBudgetController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CategoryBudgetController.class
            );

    private final CategoryBudgetService categoryBudgetService;

    public CategoryBudgetController(
            CategoryBudgetService categoryBudgetService
    ) {
        this.categoryBudgetService =
                categoryBudgetService;
    }

    // =========================================
    // CREATE CATEGORY BUDGET
    // =========================================

    @PostMapping
    public CategoryBudgetResponse createCategoryBudget(
            @Valid
            @RequestBody
            CategoryBudgetRequest request,

            Authentication authentication
    ) {

        log.info(
                "API HIT: Create Category Budget"
        );

        String email =
                authentication.getName();

        return categoryBudgetService
                .createCategoryBudget(
                        email,
                        request
                );
    }

    // =========================================
    // GET CATEGORY BUDGETS
    // =========================================

    @GetMapping("/{budgetId}")
    public List<CategoryBudgetResponse>
    getBudgetCategories(
            @PathVariable Long budgetId
    ) {

        log.info(
                "API HIT: Get Category Budgets"
        );

        return categoryBudgetService
                .getBudgetCategories(
                        budgetId
                );
    }
}