package com.rakesh.expensetracker.budget.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.budget.dto.BudgetRequest;
import com.rakesh.expensetracker.budget.dto.BudgetResponse;
import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.budget.entity.BudgetType;
import com.rakesh.expensetracker.budget.repository.BudgetRepository;
import com.rakesh.expensetracker.budget.service.BudgetService;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.UserRepository;


@Service
public class BudgetServiceImpl implements BudgetService {

    private static final Logger log =
            LoggerFactory.getLogger(BudgetServiceImpl.class);

    private final BudgetRepository budgetRepository;

    private final UserRepository userRepository;

    public BudgetServiceImpl(
            BudgetRepository budgetRepository,
            UserRepository userRepository
    ) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    // =========================================
    // CREATE BUDGET
    // =========================================

    @Override
    public BudgetResponse createBudget(
            String email,
            BudgetRequest request
    ) {

        log.info(
                "Creating budget for email={}",
                email
        );

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
        
        validateBudgetDates(
                request.getBudgetType(),
                request.getStartDate(),
                request.getEndDate()
        );

        Budget budget = new Budget();

        budget.setUser(user);

        budget.setBudgetType(
                request.getBudgetType());

        budget.setTotalBudget(
                request.getTotalBudget());
        
     // =========================================
     // ALERT THRESHOLDS
     // =========================================

     // Default warning threshold = 80%
     if (request.getWarningThreshold() == null) {

         budget.setWarningThreshold(80.0);

     } else {

         budget.setWarningThreshold(
                 request.getWarningThreshold()
         );
     }

     // Default critical threshold = 100%
     if (request.getCriticalThreshold() == null) {

         budget.setCriticalThreshold(100.0);

     } else {

         budget.setCriticalThreshold(
                 request.getCriticalThreshold()
         );
     }

        budget.setStartDate(
                request.getStartDate());

        budget.setEndDate(
                request.getEndDate());

        Budget savedBudget =
                budgetRepository.save(budget);

        log.info(
                "Budget created successfully id={}",
                savedBudget.getId()
        );

        return mapToResponse(savedBudget);
    }

    // =========================================
    // GET USER BUDGETS
    // =========================================

    @Override
    public List<BudgetResponse> getUserBudgets(
            String email
    ) {

        log.info(
                "Fetching budgets for email={}",
                email
        );

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        List<Budget> budgets =
                budgetRepository.findByUser(user);

        return budgets.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    private void validateBudgetDates(
            BudgetType budgetType,
            LocalDate startDate,
            LocalDate endDate
    ) {

        switch (budgetType) {

            case WEEKLY:

                if (!startDate.plusDays(6)
                        .equals(endDate)) {

                    throw new IllegalArgumentException(
                            "Weekly budget must have exactly 7 days range"
                    );
                }

                break;

            case MONTHLY:

                if (startDate.getYear()
                        != endDate.getYear()

                        || startDate.getMonth()
                        != endDate.getMonth()

                        || startDate.getDayOfMonth()
                        != 1

                        || endDate.getDayOfMonth()
                        != endDate.lengthOfMonth()) {

                    throw new IllegalArgumentException(
                            "Monthly budget must cover entire month"
                    );
                }

                break;

            case YEARLY:

                if (startDate.getYear()
                        != endDate.getYear()

                        || startDate.getDayOfYear()
                        != 1

                        || endDate.getDayOfYear()
                        != endDate.lengthOfYear()) {

                    throw new IllegalArgumentException(
                            "Yearly budget must cover entire year"
                    );
                }

                break;

            case CUSTOM:

                if (startDate.isAfter(endDate)) {

                    throw new IllegalArgumentException(
                            "Start date cannot be after end date"
                    );
                }

                break;
        }
    }

    // =========================================
    // MAPPER
    // =========================================

    private BudgetResponse mapToResponse(
            Budget budget
    ) {

    	BudgetResponse response =
    	        new BudgetResponse();

    	response.setId(
    	        budget.getId());

    	response.setBudgetType(
    	        budget.getBudgetType());

    	response.setTotalBudget(
    	        budget.getTotalBudget());
    	
    	response.setWarningThreshold(
    	        budget.getWarningThreshold());

    	response.setCriticalThreshold(
    	        budget.getCriticalThreshold());

    	response.setStartDate(
    	        budget.getStartDate());

    	response.setEndDate(
    	        budget.getEndDate());

    	return response;
    }
}