package com.rakesh.expensetracker.scheduler;

import java.util.List;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rakesh.expensetracker.dto.DashboardResponse;
import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.email.service.EmailService;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.DashboardService;

@Component
public class ExpenseSummaryScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ExpenseSummaryScheduler.class
            );

    private final UserRepository userRepository;

    private final DashboardService dashboardService;

    private final EmailService emailService;

    public ExpenseSummaryScheduler(
            UserRepository userRepository,
            DashboardService dashboardService,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
        this.emailService = emailService;
    }

    
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklySummary() {
        sendSummaryEmails(
                "Weekly",
                "Expense Tracker - Weekly Summary"
        );
    }

    @Scheduled(cron = "0 0 9 1 * ?")
    public void sendMonthlySummary() {
        sendSummaryEmails(
                "Monthly",
                "Expense Tracker - Monthly Summary"
        );
    }
    
    private void sendSummaryEmails(
            String period,
            String subject
    ) {

        log.info(
                "Starting {} summary scheduler",
                period
        );

        List<User> users =
                userRepository.findAll();

        for (User user : users) {

            try {

                DashboardResponse dashboard =
                        dashboardService
                                .getDashboardByEmail(
                                        user.getEmail()
                                );

                String emailBody =
                        buildSummaryEmail(
                                user,
                                dashboard,
                                period
                        );

                emailService.sendEmail(
                        user.getEmail(),
                        subject,
                        emailBody
                );

                log.info(
                        "{} summary email sent to {}",
                        period,
                        user.getEmail()
                );

            } catch (Exception ex) {

                log.error(
                        "Failed sending {} summary email to {}",
                        period,
                        user.getEmail(),
                        ex
                );
            }
        }
    }

    private String buildSummaryEmail(
            User user,
            DashboardResponse dashboard,
            String period
    ) {

        NumberFormat format =
                NumberFormat.getNumberInstance(
                        new Locale("en", "IN")
                );

        StringBuilder body =
                new StringBuilder();

        body.append("Hello ")
                .append(user.getName())
                .append(",\n\n");

        body.append("Here's a quick overview of your spending for this ");

        if ("Weekly".equalsIgnoreCase(period)) {
            body.append("week");
        } else {
            body.append("month");
        }

        body.append(".\n\n");

        body.append("===================================\n");
        body.append(period.toUpperCase())
                .append(" EXPENSE SUMMARY\n");
        body.append("===================================\n\n");

        body.append("Total Expenses     : ₹")
                .append(format.format(
                        dashboard.getTotalExpenses()
                ))
                .append("\n");

        body.append("Budget Usage       : ")
                .append(
                        dashboard.getBudgetUsagePercentage()
                )
                .append("%\n");

        body.append("Remaining Budget   : ₹")
                .append(format.format(
                        dashboard.getRemainingBudget()
                ))
                .append("\n");

        body.append("Status             : ")
                .append(
                        getBudgetStatusMessage(
                                dashboard.getBudgetStatus()
                        )
                )
                .append("\n\n");

        body.append("Top Spending Category : ")
                .append(
                        dashboard.getTopCategory()
                )
                .append("\n\n");

        if (dashboard.getHighestExpense() != null) {

            body.append("Highest Expense:\n");

            body.append(
                    dashboard
                            .getHighestExpense()
                            .getDescription()
            )
                    .append(" - ₹")
                    .append(
                            format.format(
                                    dashboard
                                            .getHighestExpense()
                                            .getAmount()
                            )
                    )
                    .append("\n\n");
        }

        body.append("Expenses By Category:\n");

        Map<String, Double> categoryExpenses =
                dashboard.getExpensesByCategory();

        if (categoryExpenses != null) {

            categoryExpenses.forEach(
                    (category, amount) ->

                            body.append("• ")
                                    .append(category)
                                    .append(" - ₹")
                                    .append(
                                            format.format(amount)
                                    )
                                    .append("\n")
            );
        }

        body.append("\n");

        body.append("Recent Expenses:\n");

        List<ExpenseDTO> recentExpenses =
                dashboard.getRecentExpenses();

        if (recentExpenses != null) {

            recentExpenses.forEach(
                    expense ->

                            body.append("• ")
                                    .append(
                                            expense.getDescription()
                                    )
                                    .append(" - ₹")
                                    .append(
                                            format.format(
                                                    expense.getAmount()
                                            )
                                    )
                                    .append("\n")
            );
        }

        body.append("\n");

        body.append("Key Insights:\n");

        List<String> insights =
                dashboard.getInsights();

        if (insights != null) {

            insights.forEach(
                    insight ->

                            body.append("• ")
                                    .append(insight)
                                    .append("\n")
            );
        }

        body.append("\n\nRegards,\n");
        body.append("Expense Tracker");

        return body.toString();
    }
    
    private String getBudgetStatusMessage(
            String budgetStatus
    ) {

        if ("SAFE".equalsIgnoreCase(budgetStatus)) {
            return "Your spending is under control";
        }

        if ("WARNING".equalsIgnoreCase(budgetStatus)) {
            return "Approaching budget limit";
        }

        if ("CRITICAL".equalsIgnoreCase(budgetStatus)) {
            return "Budget usage is at a critical level";
        }

        if ("EXCEEDED".equalsIgnoreCase(budgetStatus)) {
            return "Budget limit exceeded";
        }

        return budgetStatus;
    }
}