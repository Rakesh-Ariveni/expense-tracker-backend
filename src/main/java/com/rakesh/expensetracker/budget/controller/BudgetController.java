package com.rakesh.expensetracker.budget.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.rakesh.expensetracker.budget.dto.BudgetRequest;
import com.rakesh.expensetracker.budget.dto.BudgetResponse;
import com.rakesh.expensetracker.budget.service.BudgetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private static final Logger log =
            LoggerFactory.getLogger(BudgetController.class);

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService
    ) {
        this.budgetService = budgetService;
    }

    // =========================================
    // CREATE BUDGET
    // =========================================

    @PostMapping
    public BudgetResponse createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication
    ) {

        log.info("API HIT: Create Budget");

        String email =
                authentication.getName();

        return budgetService.createBudget(
                email,
                request
        );
    }

    // =========================================
    // GET USER BUDGETS
    // =========================================

    @GetMapping
    public List<BudgetResponse> getBudgets(
            Authentication authentication
    ) {

        log.info("API HIT: Get Budgets");

        String email =
                authentication.getName();

        return budgetService.getUserBudgets(
                email
        );
    }
}