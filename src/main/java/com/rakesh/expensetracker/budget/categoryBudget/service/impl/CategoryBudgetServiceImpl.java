package com.rakesh.expensetracker.budget.categoryBudget.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.budget.categoryBudget.dto.CategoryBudgetRequest;
import com.rakesh.expensetracker.budget.categoryBudget.dto.CategoryBudgetResponse;
import com.rakesh.expensetracker.budget.categoryBudget.entity.categoryBudget;
import com.rakesh.expensetracker.budget.categoryBudget.repository.CategoryBudgetRepository;
import com.rakesh.expensetracker.budget.categoryBudget.service.CategoryBudgetService;
import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.budget.repository.BudgetRepository;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.CategoryRepository;
import com.rakesh.expensetracker.repository.UserRepository;

@Service
public class CategoryBudgetServiceImpl
        implements CategoryBudgetService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CategoryBudgetServiceImpl.class
            );

    private final CategoryBudgetRepository categoryBudgetRepository;

    private final BudgetRepository budgetRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    public CategoryBudgetServiceImpl(
            CategoryBudgetRepository categoryBudgetRepository,
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.categoryBudgetRepository =
                categoryBudgetRepository;

        this.budgetRepository =
                budgetRepository;

        this.categoryRepository =
                categoryRepository;

        this.userRepository =
                userRepository;
    }

    // =========================================
    // CREATE CATEGORY BUDGET
    // =========================================

    @Override
    public CategoryBudgetResponse createCategoryBudget(
            String email,
            CategoryBudgetRequest request
    ) {

        log.info(
                "Creating category budget for email={}",
                email
        );

        User user =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Budget budget =
                budgetRepository.findById(
                        request.getBudgetId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found"
                        )
                );

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );

        // =========================================
        // VALIDATE BUDGET OWNER
        // =========================================

        if (!budget.getUser().getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "Budget does not belong to user"
            );
        }

        // =========================================
        // PREVENT DUPLICATE CATEGORY
        // =========================================

        categoryBudget existing =
                categoryBudgetRepository
                .findByBudgetAndCategory(
                        budget,
                        category
                );

        if (existing != null) {

            throw new RuntimeException(
                    "Category already allocated in budget"
            );
        }

        categoryBudget categoryBudget =
                new categoryBudget();

        categoryBudget.setBudget(budget);

        categoryBudget.setCategory(category);

        categoryBudget.setAllocatedAmount(
                request.getAllocatedAmount()
        );

        categoryBudget saved =
                categoryBudgetRepository.save(
                        categoryBudget
                );

        log.info(
                "Category budget created successfully id={}",
                saved.getId()
        );

        return mapToResponse(saved);
    }

    // =========================================
    // GET CATEGORY BUDGETS
    // =========================================

    @Override
    public List<CategoryBudgetResponse> getBudgetCategories(
            Long budgetId
    ) {

        Budget budget =
                budgetRepository.findById(
                        budgetId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Budget not found"
                        )
                );

        return categoryBudgetRepository
                .findByBudget(budget)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================
    // MAPPER
    // =========================================

    private CategoryBudgetResponse mapToResponse(
            categoryBudget categoryBudget
    ) {

        CategoryBudgetResponse response =
                new CategoryBudgetResponse();

        response.setId(
                categoryBudget.getId()
        );

        response.setBudgetId(
                categoryBudget.getBudget().getId()
        );

        response.setCategoryId(
                categoryBudget.getCategory().getId()
        );

        response.setCategoryName(
                categoryBudget.getCategory().getName()
        );

        response.setAllocatedAmount(
                categoryBudget.getAllocatedAmount()
        );

        return response;
    }
}