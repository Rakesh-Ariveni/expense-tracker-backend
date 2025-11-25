package com.rakesh.expensetracker.service.impl;

import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    // Constructor injection
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public Expense updateExpense(Expense expense) {
        if (!expenseRepository.existsById(expense.getId())) {
            throw new RuntimeException("Expense not found: " + expense.getId());
        }
        return expenseRepository.save(expense);
    }

    @Override
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found: " + id);
        }
        expenseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUser(User user) {
        return expenseRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getRecentExpenses(User user, int limit) {
        List<Expense> list = expenseRepository.findByUser(user);
        list.sort(Comparator.comparing(Expense::getExpenseDate).reversed()); // uses Lombok getter
        return list.subList(0, Math.min(limit, list.size()));
    }
}
