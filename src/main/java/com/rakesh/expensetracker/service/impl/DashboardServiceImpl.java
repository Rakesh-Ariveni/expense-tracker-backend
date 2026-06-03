package com.rakesh.expensetracker.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.budget.categoryBudget.entity.categoryBudget;
import com.rakesh.expensetracker.budget.categoryBudget.repository.CategoryBudgetRepository;
import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.budget.entity.BudgetType;
import com.rakesh.expensetracker.budget.repository.BudgetRepository;
import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.DashboardService;
import com.rakesh.expensetracker.service.MonitoringService;
import com.rakesh.expensetracker.service.analytics.AnalyticsService;

@Service
public class DashboardServiceImpl
        implements DashboardService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DashboardServiceImpl.class
            );

    private final MonitoringService monitoringService;

    private final ExpenseRepository expenseRepository;

    private final UserRepository userRepository;

    private final AnalyticsService analyticsService;

    private final BudgetRepository budgetRepository;

    private final CategoryBudgetRepository
            categoryBudgetRepository;

    public DashboardServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            MonitoringService monitoringService,
            AnalyticsService analyticsService,
            BudgetRepository budgetRepository,
            CategoryBudgetRepository categoryBudgetRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.monitoringService = monitoringService;
        this.analyticsService = analyticsService;
        this.budgetRepository = budgetRepository;
        this.categoryBudgetRepository =
                categoryBudgetRepository;
    }

    @Override
    @Cacheable(value = "dashboard", key = "#email")
    public DashboardResponse getDashboardByEmail(
            String email
    ) {

        monitoringService.incrementApi(
                "dashboard"
        );

        log.info(
                "Fetching dashboard for email={}",
                email
        );

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {

                    log.error(
                            "User not found for dashboard email={}",
                            email
                    );

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });

        DashboardResponse response =
                new DashboardResponse();

        response.setUserId(user.getId());

        // =========================================
        // 1️⃣ TOTAL EXPENSES
        // =========================================

        Double totalExpenses = 0.0;

        try {

            log.info(
                    "Fetching total expenses from Redis for userId={}",
                    user.getId()
            );
            
            totalExpenses =
                    analyticsService.getTotalExpenses(
                            user.getId()
                    );
            
            if (totalExpenses != null &&
                    totalExpenses > 0.0) {

                monitoringService.cacheHit();

            } else {

                monitoringService.cacheMiss();
                
                log.info(
                        "Redis cache miss for total expenses userId={}",
                        user.getId()
                );

                totalExpenses =
                        expenseRepository
                                .findTotalExpensesByUser(user);
            }

                if (totalExpenses == null) {
                    totalExpenses = 0.0;
                }

        } catch (Exception ex) {

            log.error(
                    "Redis failure while fetching total expenses. Falling back to MySQL userId={}",
                    user.getId(),
                    ex
            );

            try {

                totalExpenses =
                        expenseRepository
                                .findTotalExpensesByUser(
                                        user
                                );

                if (totalExpenses == null) {
                    totalExpenses = 0.0;
                }

            } catch (Exception dbEx) {

                log.error(
                        "MySQL fallback failed for total expenses userId={}",
                        user.getId(),
                        dbEx
                );

                totalExpenses = 0.0;
            }
        }

        response.setTotalExpenses(
                totalExpenses
        );

        // =========================================
        // 1B️⃣ BUDGET SUMMARY
        // =========================================

        try {

            List<Budget> budgets =
                    budgetRepository.findByUser(
                            user
                    );

            if (!budgets.isEmpty()) {

                Budget activeBudget =
                        budgets.get(0);

                Double filteredExpenses =
                        calculateBudgetTypeExpenses(
                                user,
                                activeBudget
                        );

                Double totalBudget =
                        activeBudget.getTotalBudget();

                Double remainingBudget =
                        totalBudget - filteredExpenses;

                double usagePercentage =
                        (filteredExpenses / totalBudget)
                                * 100;

                response.setTotalBudget(
                        totalBudget
                );

                response.setRemainingBudget(
                        remainingBudget
                );

                response.setBudgetUsagePercentage(
                        usagePercentage
                );

                if (usagePercentage >=
                        activeBudget
                                .getCriticalThreshold()) {

                    response.setBudgetStatus(
                            "CRITICAL"
                    );

                } else if (usagePercentage >=
                        activeBudget
                                .getWarningThreshold()) {

                    response.setBudgetStatus(
                            "WARNING"
                    );

                } else {

                    response.setBudgetStatus(
                            "SAFE"
                    );
                }
            }

        } catch (Exception ex) {

            log.error(
                    "Failed to calculate budget summary userId={}",
                    user.getId(),
                    ex
            );
        }

        // =========================================
        // 2️⃣ CATEGORY ANALYTICS
        // =========================================

        Map<String, Double> byCategory =
                new HashMap<>();

        try {

            log.debug(
                    "Fetching category analytics from Redis for userId={}",
                    user.getId()
            );

            Map<Object, Object> redisAnalytics =
                    analyticsService
                            .getCategoryAnalytics(
                                    user.getId()
                            );

            if (redisAnalytics != null &&
                    !redisAnalytics.isEmpty()) {
            	
            	monitoringService.cacheHit();
            	
                redisAnalytics.forEach((k, v) -> {

                    byCategory.put(
                            k.toString(),
                            Double.valueOf(
                                    v.toString()
                            )
                    );
                });

            } else {

                log.info(
                        "Redis cache miss for category analytics userId={}",
                        user.getId()
                );
                
                monitoringService.cacheMiss();

                for (Object[] row :
                        expenseRepository
                                .findExpensesByCategory(
                                        user
                                )) {

                    byCategory.put(
                            (String) row[0],
                            (Double) row[1]
                    );
                }
            }

        } catch (Exception ex) {

            log.error(
                    "Redis failure while fetching category analytics. Falling back to MySQL userId={}",
                    user.getId(),
                    ex
            );

            try {

                for (Object[] row :
                        expenseRepository
                                .findExpensesByCategory(
                                        user
                                )) {

                    byCategory.put(
                            (String) row[0],
                            (Double) row[1]
                    );
                }

            } catch (Exception dbEx) {

                log.error(
                        "MySQL fallback failed for category analytics userId={}",
                        user.getId(),
                        dbEx
                );
            }
        }

        response.setExpensesByCategory(
                byCategory
        );

        // =========================================
        // CATEGORY BUDGET INSIGHTS
        // =========================================

        try {

            List<Budget> budgets =
                    budgetRepository.findByUser(
                            user
                    );

            if (!budgets.isEmpty()) {

                Budget activeBudget =
                        budgets.get(0);

                List<categoryBudget> categoryBudgets =
                        categoryBudgetRepository
                                .findByBudget(
                                        activeBudget
                                );

                Map<String, Object> insights =
                        new HashMap<>();

                List<String> warningCategories =
                        new ArrayList<>();

                List<String> criticalCategories =
                        new ArrayList<>();

                List<String> overspendingCategories =
                        new ArrayList<>();

                for (categoryBudget categoryBudget
                        : categoryBudgets) {

                    String categoryName =
                            categoryBudget
                                    .getCategory()
                                    .getName();

                    Double allocated =
                            categoryBudget
                                    .getAllocatedAmount();

                    Double spent =
                            analyticsService.getCategoryTotal(
                                    user.getId(),
                                    categoryName
                            );

                    if (spent == null || spent <= 0.0) {

                        spent =
                                byCategory.getOrDefault(
                                        categoryName,
                                        0.0
                                );
                    }

                    Double remaining =
                            allocated - spent;

                    double usage =
                            (spent / allocated)
                                    * 100;

                    Map<String, Object> categoryData =
                            new HashMap<>();

                    categoryData.put(
                            "allocated",
                            allocated
                    );

                    categoryData.put(
                            "spent",
                            spent
                    );

                    categoryData.put(
                            "remaining",
                            remaining
                    );

                    categoryData.put(
                            "usagePercentage",
                            usage
                    );

                    insights.put(
                            categoryName,
                            categoryData
                    );

                    if (usage > 100) {

                        overspendingCategories.add(
                                categoryName
                        );

                    } else if (usage >= 100) {

                        criticalCategories.add(
                                categoryName
                        );

                    } else if (usage >= 80) {

                        warningCategories.add(
                                categoryName
                        );
                    }                
                }

                response.setCategoryBudgetInsights(
                        insights
                );

                response.setWarningCategories(
                        warningCategories
                );

                response.setCriticalCategories(
                        criticalCategories
                );

                response.setOverspendingCategories(
                        overspendingCategories
                );
            }

        } catch (Exception ex) {

            log.error(
                    "Failed category budget insights userId={}",
                    user.getId(),
                    ex
            );
        }

        // =========================================
        // 3️⃣ TOP CATEGORY
        // =========================================

        String topCategory = null;

        double maxAmount = 0;

        for (Map.Entry<String, Double> entry :
                byCategory.entrySet()) {

            if (entry.getValue() > maxAmount) {

                maxAmount = entry.getValue();

                topCategory = entry.getKey();
            }
        }

        response.setTopCategory(
                topCategory
        );

        // =========================================
        // 4️⃣ WEEKLY TRENDS
        // =========================================

        Map<String, Double> weeklyTrend =
                new LinkedHashMap<>();

        try {

            log.debug(
                    "Calculating weekly trends for userId={}",
                    user.getId()
            );

            LocalDate startDate =
                    LocalDate.now()
                            .minusDays(6);

            for (Object[] row :
                    expenseRepository
                            .findWeeklyTrend(
                                    user,
                                    startDate
                            )) {

                weeklyTrend.put(
                        row[0].toString(),
                        (Double) row[1]
                );
            }

        } catch (Exception ex) {

            log.error(
                    "Failed to fetch weekly trends userId={}",
                    user.getId(),
                    ex
            );
        }

        response.setWeeklyTrends(
                weeklyTrend
        );

        // =========================================
        // 5️⃣ MOST FREQUENT CATEGORY
        // =========================================

        try {

            String mostFrequentCategory =
                    expenseRepository
                            .findMostFrequentCategory(
                                    user,
                                    PageRequest.of(0, 1)
                            )
                            .stream()
                            .findFirst()
                            .orElse(null);

            response.setMostFrequentCategory(
                    mostFrequentCategory
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to fetch most frequent category userId={}",
                    user.getId(),
                    ex
            );

            response.setMostFrequentCategory(
                    null
            );
        }

        // =========================================
        // 7️⃣ RECENT EXPENSES
        // =========================================

        try {

            List<Expense> recentExpenses =
                    expenseRepository.findRecentExpenses(
                            user,
                            PageRequest.of(0, 100)
                    );

            response.setRecentExpenses(
                    mapToDTO(
                            recentExpenses.stream()
                                    .limit(5)
                                    .toList()
                    )
            );

            Expense highestExpense =
                    recentExpenses.stream()
                            .max((e1, e2) ->
                                    Double.compare(
                                            e1.getAmount(),
                                            e2.getAmount()
                                    ))
                            .orElse(null);

            if (highestExpense != null) {

                response.setHighestExpense(
                        new ExpenseDTO(
                                highestExpense.getId(),
                                highestExpense.getAmount(),
                                highestExpense.getDescription(),
                                highestExpense.getExpenseDate(),
                                highestExpense.getCategory().getName()
                        )
                );
            }

        } catch (Exception ex) {

            log.error(
                    "Failed to fetch recent expenses userId={}",
                    user.getId(),
                    ex
            );

            response.setRecentExpenses(
                    List.of()
            );
        }
        
        List<String> insights =
                generateInsights(response);

        response.setInsights(insights);
        
        log.info(
                "Dashboard fetched successfully for userId={}",
                user.getId()
        );

        return response;
    }

    private Double calculateBudgetTypeExpenses(
            User user,
            Budget budget
    ) {

        try {

            Double expenses =
                    expenseRepository
                            .findTotalExpensesBetweenDates(
                                    user,
                                    budget.getStartDate(),
                                    budget.getEndDate()
                            );

            return expenses != null
                    ? expenses
                    : 0.0;

        } catch (Exception ex) {

            log.error(
                    "Failed budget calculation userId={}",
                    user.getId(),
                    ex
            );

            return 0.0;
        }
    }
    
    private List<String> generateInsights(
            DashboardResponse response
    ) {

        List<String> insights =
                new ArrayList<>();

        // =====================================
        // TOP CATEGORY
        // =====================================

        if (response.getTopCategory() != null) {

            insights.add(
                    response.getTopCategory()
                            + " is your highest spending category."
            );
        }

        // =====================================
        // OVERALL BUDGET
        // =====================================

        if (response.getBudgetUsagePercentage() != null) {

            double usage =
                    response.getBudgetUsagePercentage();

            if (usage > 100) {

                insights.add(
                        "You have exceeded your overall budget by "
                                + String.format(
                                "%.2f",
                                usage - 100
                        )
                                + "%."
                );

            } else if (usage >= 80) {

                insights.add(
                        "Your overall budget usage is at "
                                + String.format(
                                "%.2f",
                                usage
                        )
                                + "%."
                );
            }
        }

        // =====================================
        // OVERSPENDING CATEGORIES
        // =====================================

        if (response.getOverspendingCategories()
                != null) {

            for (String category :
                    response.getOverspendingCategories()) {

                insights.add(
                        category
                                + " exceeded its allocated budget."
                );
            }
        }
        
     // =====================================
     // CRITICAL CATEGORIES
     // =====================================

     if (response.getCriticalCategories()
             != null) {

         for (String category :
                 response.getCriticalCategories()) {

             insights.add(
                     category
                             + " has reached its allocated budget."
             );
         }
     }

        // =====================================
        // WARNING CATEGORIES
        // =====================================

        if (response.getWarningCategories()
                != null) {

            for (String category :
                    response.getWarningCategories()) {

                insights.add(
                        category
                                + " is approaching its budget limit."
                );
            }
        }

        return insights;
    }

    private List<ExpenseDTO> mapToDTO(
            List<Expense> expenses
    ) {

        return expenses.stream()
                .map(e -> new ExpenseDTO(
                        e.getId(),
                        e.getAmount(),
                        e.getDescription(),
                        e.getExpenseDate(),
                        e.getCategory().getName()
                ))
                .toList();
    }
}