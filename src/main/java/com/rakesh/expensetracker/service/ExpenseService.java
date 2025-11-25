package com.rakesh.expensetracker.service;

import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;

import java.util.List;

public interface ExpenseService {
    Expense addExpense(Expense expense);
    Expense updateExpense(Expense expense);
    void deleteExpense(Long id);
    List<Expense> getExpensesByUser(User user);
    List<Expense> getRecentExpenses(User user, int limit);
}
