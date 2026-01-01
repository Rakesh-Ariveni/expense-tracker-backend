package com.rakesh.expensetracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;

public interface ExpenseService {
    Expense addExpense(Expense expense);
    Expense updateExpense(Expense expense);
    void deleteExpense(Long id);
    List<Expense> getExpensesByUser(User user);
    List<Expense> getRecentExpenses(User user, int limit);
    Page<ExpenseDTO> getExpenses(
            String email,
            int page,
            int size,
            String sortBy,
            String direction,
            String category,
            LocalDateTime from,
            LocalDateTime to
    );
}

