package com.rakesh.expensetracker;

import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.service.UserService;
import com.rakesh.expensetracker.service.CategoryService;
import com.rakesh.expensetracker.service.ExpenseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@EnableCaching
@SpringBootApplication
public class ExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerApplication.class, args);
    }

//    // ✅ Quick Test Runner
//    @Bean
//    CommandLineRunner run(UserService userService,
//                          CategoryService categoryService,
//                          ExpenseService expenseService) {
//        return args -> {
//            // Create User
//            User user = new User();
//            user.setName("Rakesh");
//            user.setEmail("rakesh@test.com");
//            user.setPassword("12345");
//            userService.registerUser(user);
//
//            // Create Category
//            Category food = new Category();
//            food.setName("Food");
//            categoryService.addCategory(food);
//
//            // Add Expense
//            Expense expense = new Expense();
//            expense.setAmount(200.0);
//            expense.setDescription("Lunch");
//            expense.setExpenseDate(LocalDateTime.now());
//            expense.setUser(user);
//            expense.setCategory(food);
//            expenseService.addExpense(expense);
//
//            // Fetch Expenses
//            System.out.println("Expenses for user: " + expenseService.getExpensesByUser(user));
//        };
//    }
}
