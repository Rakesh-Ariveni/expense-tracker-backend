package com.rakesh.expensetracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.AsyncService;
import com.rakesh.expensetracker.service.ExpenseService;
import com.rakesh.expensetracker.service.MonitoringService;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AsyncService asyncService;
    private final MonitoringService monitoringService;
    private static final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("expenseDate", "amount", "createdAt");

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            AsyncService asyncService,
            MonitoringService monitoringService
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.asyncService = asyncService;
        this.monitoringService = monitoringService;
    }

    @Override
    @CacheEvict(value = "dashboard", key = "#expense.user.email")
    public Expense addExpense(Expense expense) {

        log.info("Adding expense for userId={}, amount={}",
                expense.getUser().getId(),
                expense.getAmount());
        monitoringService.incrementApi("add_expense");
        Expense saved = expenseRepository.save(expense);
        
        // 🔥 async call (non-blocking)
        asyncService.logExpenseCreation(saved.getId());

        log.info("Expense created successfully id={}", saved.getId());

        return saved;
    }

    @Override
    @CacheEvict(value = "dashboard", key = "#expense.user.email")
    public Expense updateExpense(Expense expense) {

        log.info("Updating expense id={}", expense.getId());
        monitoringService.incrementApi("get_expenses");
        if (!expenseRepository.existsById(expense.getId())) {
            log.error("Expense not found id={}", expense.getId());
            throw new ResourceNotFoundException("Expense not found: " + expense.getId());
        }

        Expense updated = expenseRepository.save(expense);

        log.info("Expense updated successfully id={}", updated.getId());

        return updated;
    }

    @Override
    @CacheEvict(value = "dashboard", allEntries = true)
    public void deleteExpense(Long id) {

        log.info("Deleting expense id={}", id);

        if (!expenseRepository.existsById(id)) {
            log.error("Expense not found for deletion id={}", id);
            throw new ResourceNotFoundException("Expense not found: " + id);
        }

        expenseRepository.deleteById(id);

        log.info("Expense deleted successfully id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUser(User user) {

        log.debug("Fetching all expenses for userId={}", user.getId());

        return expenseRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Expense> getRecentExpenses(User user, int limit) {

        log.debug("Fetching recent expenses for userId={}, limit={}",
                user.getId(), limit);

        return expenseRepository.findRecentExpenses(user, PageRequest.of(0, limit));
    }

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

        log.info("Fetching expenses email={}, page={}, size={}, sortBy={}, direction={}, category={}, from={}, to={}",
                email, page, size, sortBy, direction, category, from, to);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found email={}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            log.warn("Invalid sortBy field={}, defaulting to expenseDate", sortBy);
            sortBy = "expenseDate";
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Expense> expenses;

        if (category != null && !category.isBlank()) {
            log.debug("Filtering by category={}", category);
            expenses = expenseRepository
                    .findByUserAndCategory_Name(user, category, pageable);

        } else if (from != null && to != null) {
            log.debug("Filtering by date range from={} to={}", from, to);
            expenses = expenseRepository
                    .findByUserAndExpenseDateBetween(user, from, to, pageable);

        } else {
            log.debug("Fetching all expenses without filters");
            expenses = expenseRepository.findByUser(user, pageable);
        }

        log.info("Expenses fetched successfully for userId={}", user.getId());

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