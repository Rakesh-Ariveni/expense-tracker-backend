package com.rakesh.expensetracker.notification.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.budget.categoryBudget.entity.categoryBudget;
import com.rakesh.expensetracker.budget.categoryBudget.repository.CategoryBudgetRepository;
import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.budget.repository.BudgetRepository;
import com.rakesh.expensetracker.email.service.EmailService;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;
import com.rakesh.expensetracker.notification.service.NotificationService;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.analytics.AnalyticsService;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    NotificationServiceImpl.class
            );

    private final BudgetRepository budgetRepository;

    private final UserRepository userRepository;
    
    private final StringRedisTemplate redisTemplate;

    private final AnalyticsService analyticsService;
    
    private final CategoryBudgetRepository categoryBudgetRepository;
    
    private final EmailService emailService;

    public NotificationServiceImpl(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            StringRedisTemplate redisTemplate,
            AnalyticsService analyticsService,
            CategoryBudgetRepository categoryBudgetRepository,
            EmailService emailService
    ) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.analyticsService = analyticsService;
        this.categoryBudgetRepository = categoryBudgetRepository;
		this.emailService = emailService;
    }

    @Override
    public void processBudgetAlert(
            ExpenseCreatedEvent event
    ) {

        Long userId = event.getUserId();

        log.info(
                "Processing budget notification for userId={}",
                userId
        );

        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {

            log.warn(
                    "User not found for notification userId={}",
                    userId
            );

            return;
        }

        List<Budget> budgets =
                budgetRepository.findByUser(user);

        if (budgets.isEmpty()) {

            log.info(
                    "No budgets configured for userId={}",
                    userId
            );

            return;
        }

        Double totalExpenses =
                analyticsService.getTotalExpenses(userId);

        for (Budget budget : budgets) {

            Double budgetLimit =
                    budget.getTotalBudget();

            double percentage =
                    (totalExpenses / budgetLimit) * 100;

            log.info(
                    "Budget usage for userId={} is {}%",
                    userId,
                    percentage
            );

            Double warningThreshold =
                    budget.getWarningThreshold();

            Double criticalThreshold =
                    budget.getCriticalThreshold();

            String warningKey =
                    "budget:alert:user:"
                            + userId
                            + ":warning";

            String criticalKey =
                    "budget:alert:user:"
                            + userId
                            + ":critical";

            // =========================================
            // WARNING ALERT
            // =========================================

            if (percentage >= warningThreshold
                    && percentage < criticalThreshold) {

                Boolean alreadySent =
                        redisTemplate.hasKey(warningKey);

                if (Boolean.FALSE.equals(alreadySent)) {

                    log.warn(
                            "ALERT: User {} crossed warning threshold {}%",
                            userId,
                            warningThreshold
                    );
                    
                    Double remaining =
                            budgetLimit - totalExpenses;

                    emailService.sendEmail(
                            user.getEmail(),
                            "Expense Tracker - Budget Warning",
                            "Hello " + user.getName()
                            + ",\n\nYou have used "
                            + String.format("%.2f", percentage)
                            + "% of your total budget.\n\n"
                            + "Total Budget: ₹" + budgetLimit
                            + "\nSpent So Far: ₹" + totalExpenses
                            + "\nRemaining Budget: ₹" + remaining
                            + "\n\nPlease keep an eye on your spending."
                    );

                    redisTemplate.opsForValue().set(
                            warningKey,
                            "sent"
                    );
                }
            }

            // =========================================
            // CRITICAL ALERT
            // =========================================

            if (percentage >= criticalThreshold) {

                Boolean alreadySent =
                        redisTemplate.hasKey(criticalKey);

                if (Boolean.FALSE.equals(alreadySent)) {

                    log.error(
                            "ALERT: User {} crossed critical threshold {}%",
                            userId,
                            criticalThreshold
                    );
                    
                    Double remaining =
                            budgetLimit - totalExpenses;

                    emailService.sendEmail(
                            user.getEmail(),
                            "Expense Tracker - High Budget Usage Alert",
                            "Hello " + user.getName()
                            + ",\n\nYou have crossed the critical budget threshold.\n\n"
                            + "Total Budget: ₹" + budgetLimit
                            + "\nSpent So Far: ₹" + totalExpenses
                            + "\nRemaining Budget: ₹" + remaining
                            + "\n\nPlease review your spending carefully."
                    );

                    redisTemplate.opsForValue().set(
                            criticalKey,
                            "sent"
                    );
                }
            }
            
         // =========================================
         // CATEGORY BUDGET ALERTS
         // =========================================

            List<categoryBudget> categoryBudgets =
                    categoryBudgetRepository
                            .findByBudgetWithCategory(budget);
                  
         try {

        	 for (categoryBudget categoryBudget
        			 : categoryBudgets) {

        		 String categoryName =
        				 categoryBudget
        				 .getCategory()
        				 .getName();

        		 Double categorySpent =
        				 analyticsService
        				 .getCategoryTotal(
        						 userId,  
        						 categoryName
        						 );

        		 Double categoryLimit =
        				 categoryBudget
        				 .getAllocatedAmount();

        		 double categoryPercentage =
        				 (categorySpent / categoryLimit)
        				 * 100;

        		 log.info(
        				 "Category {} usage for userId={} is {}%",
        				 categoryName,
        				 userId,
        				 categoryPercentage
        				 );

        		 String categoryWarningKey =
        				 "category:alert:user:"
        						 + userId
        						 + ":"
        						 + categoryName
        						 + ":warning";
        		 
        		 String categoryFullyUtilizedKey =
        			        "category:alert:user:"
        			                + userId
        			                + ":"
        			                + categoryName
        			                + ":fully-utilized";

        		 String categoryExceededKey =
        				 "category:alert:user:"
        						 + userId
        						 + ":"
        						 + categoryName
        						 + ":exceeded";

        		 // =====================================
        		 // CATEGORY WARNING
        		 // =====================================

        		 if (categoryPercentage >= 80
        				 && categoryPercentage < 100) {

        			 Boolean alreadySent =
        					 redisTemplate.hasKey(
        							 categoryWarningKey
        							 );

        			 if (Boolean.FALSE.equals(alreadySent)) {

        				 log.warn(
        						    "CATEGORY ALERT: User {} used {}% of {} budget",
        						    userId,
        						    String.format("%.2f", categoryPercentage),
        						    categoryName
        						);

        				 Double remaining =
        					        categoryLimit - categorySpent;

        					emailService.sendEmail(
        					        user.getEmail(),
        					        "Expense Tracker - Category Budget Alert",
        					        "Hello " + user.getName()
        					        + ",\n\nYou have already used "
        					        + String.format("%.2f", categoryPercentage)
        					        + "% of your "
        					        + categoryName
        					        + " budget.\n\n"
        					        + "Allocated Budget: ₹" + categoryLimit
        					        + "\nSpent So Far: ₹" + categorySpent
        					        + "\nRemaining Budget: ₹" + remaining
        					        + "\n\nConsider tracking upcoming expenses in this category."
        					);

        				 redisTemplate.opsForValue().set(
        						 categoryWarningKey,
        						 "sent"
        						 );
        			 }
        		 }
        		 
        		 // =====================================
        		 // CATEGORY FULLY UTILIZED
        		 // =====================================
        		 
        		 if (Math.abs(categoryPercentage - 100.0) < 0.01) {

        			    Boolean alreadySent =
        			            redisTemplate.hasKey(
        			                    categoryFullyUtilizedKey
        			            );

        			    if (Boolean.FALSE.equals(alreadySent)) {

        			        log.warn(
        			                "CATEGORY ALERT: User {} fully utilized {} budget",
        			                userId,
        			                categoryName
        			        );

        			        emailService.sendEmail(
        			                user.getEmail(),
        			                "Expense Tracker - Category Budget Fully Utilized",
        			                "Hello " + user.getName()
        			                + ",\n\nYou have fully utilized your "
        			                + categoryName
        			                + " budget.\n\n"
        			                + "Allocated Budget: ₹" + categoryLimit
        			                + "\nSpent So Far: ₹" + categorySpent
        			                + "\n\nAny additional expense in this category will exceed your planned budget."
        			        );

        			        redisTemplate.opsForValue().set(
        			                categoryFullyUtilizedKey,
        			                "sent"
        			        );
        			    }
        			}

        		 // =====================================
        		 // CATEGORY EXCEEDED
        		 // =====================================

        		 if (categoryPercentage > 100) {

        			 Boolean alreadySent =
        					 redisTemplate.hasKey(
        							 categoryExceededKey
        							 );

        			 if (Boolean.FALSE.equals(alreadySent)) {

        				 log.error(
        						 "CATEGORY ALERT: User {} exceeded {} budget",
        						 userId,
        						 categoryName
        						 );

        				 Double exceededBy =
        					        categorySpent - categoryLimit;

        					emailService.sendEmail(
        					        user.getEmail(),
        					        "Expense Tracker - Category Budget Exceeded",
        					        "Hello " + user.getName()
        					        + ",\n\nYour "
        					        + categoryName
        					        + " spending has exceeded the planned budget.\n\n"
        					        + "Allocated Budget: ₹" + categoryLimit
        					        + "\nSpent So Far: ₹" + categorySpent
        					        + "\nExceeded By: ₹" + exceededBy
        					        + "\n\nYou may want to review your spending in this category."
        					);

        				 redisTemplate.opsForValue().set(
        						 categoryExceededKey,
        						 "sent"
        						 );
        			 }
        		 }
        	 }

         } catch (Exception ex) {

        	 log.error(
        			 "Category alert processing failed for userId={}",
        			 userId,
        			 ex
        			 );
         }
        }
    }
}