package com.rakesh.expensetracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.ExpenseService;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    // ✅ ALLOWED SORT FIELDS (IMPORTANT)
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("expenseDate", "amount", "createdAt");

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
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
        return expenseRepository.findRecentExpenses(user, PageRequest.of(0, limit));
    }

    // =========================
    // PAGINATION + FILTER + SORT
    // =========================
    @Override
    public Page<ExpenseDTO> getExpenses(
            String email,
            int page,
            int size,
            String sortBy,
            String direction,
            String category,
            LocalDateTime from,
            LocalDateTime to
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ SAFE SORTING
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "expenseDate"; // default safe field
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Expense> expenses;

        if (category != null && !category.isBlank()) {
            expenses = expenseRepository
                    .findByUserAndCategory_Name(user, category, pageable);
        } else if (from != null && to != null) {
            expenses = expenseRepository
                    .findByUserAndExpenseDateBetween(user, from, to, pageable);
        } else {
            expenses = expenseRepository.findByUser(user, pageable);
        }

        return expenses.map(this::toDTO);
    }

    private ExpenseDTO toDTO(Expense e) {
        return new ExpenseDTO(
                e.getId(),
                e.getAmount(),
                e.getDescription(),
                e.getExpenseDate(),
                e.getCategory().getName()
        );
    }
}
