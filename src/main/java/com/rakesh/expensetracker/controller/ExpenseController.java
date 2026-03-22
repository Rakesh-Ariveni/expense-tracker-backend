package com.rakesh.expensetracker.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.dto.ExpenseRequest;
import com.rakesh.expensetracker.dto.ExpenseResponse;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.service.CategoryService;
import com.rakesh.expensetracker.service.ExpenseService;
import com.rakesh.expensetracker.service.IdempotencyService;
import com.rakesh.expensetracker.service.RateLimitService;
import com.rakesh.expensetracker.service.UserService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private static final Logger log = LoggerFactory.getLogger(ExpenseController.class);

    private final ExpenseService expenseService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final IdempotencyService idempotencyService;
    private final RateLimitService rateLimitService;

    public ExpenseController(ExpenseService expenseService, UserService userService, CategoryService categoryService, IdempotencyService idempotencyService, RateLimitService rateLimitService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.idempotencyService = idempotencyService;
		this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@RequestBody ExpenseRequest req, @RequestHeader(value = "Idempotency-Key", required = false) String key, Authentication authentication) {

        log.info("API HIT: Add Expense");
        log.info("🔥 ExpenseController hit");
        
        String email = authentication.getName();

        // 🔥 RATE LIMIT CHECK
        if (!rateLimitService.isAllowed(email, 5, 1)) {
            throw new RuntimeException("Too many requests. Please try again later.");
        }
        
        if (key == null || key.isBlank()) {
            throw new RuntimeException("Missing Idempotency-Key");
        }
        
        // 🔥 IDEMPOTENCY CHECK
        if (idempotencyService.isDuplicate(key)) {
            throw new RuntimeException("Duplicate request detected");
        }

        idempotencyService.saveKey(key);

        User user = userService.getUserById(req.getUserId());

        Category category = categoryService
                .getCategoryByName(req.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Expense e = new Expense();
        e.setAmount(req.getAmount());
        e.setDescription(req.getDescription());
        e.setExpenseDate(req.getExpenseDate());
        e.setUser(user);
        e.setCategory(category);

        Expense saved = expenseService.addExpense(e);

        ExpenseResponse resp = new ExpenseResponse();
        resp.setId(saved.getId());
        resp.setAmount(saved.getAmount());
        resp.setDescription(saved.getDescription());
        resp.setExpenseDate(saved.getExpenseDate());
        resp.setUserId(user.getId());
        resp.setCategoryId(category.getId());

        log.info("Expense created successfully id={}", saved.getId());

        return ResponseEntity.status(201).body(resp);
    }
    
    @GetMapping
    public Page<ExpenseDTO> getExpenses(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to
    ) {

        log.info("API HIT: Get Expenses page={}, size={}", page, size);

        String email = authentication.getName();

        return expenseService.getExpenses(
                email, page, size, sortBy, direction, category, from, to
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByUser(@PathVariable Long userId) {

        log.info("API HIT: Get Expenses by userId={}", userId);

        User user = userService.getUserById(userId);

        List<ExpenseResponse> list = expenseService.getExpensesByUser(user)
                .stream().map(e -> {
                    ExpenseResponse resp = new ExpenseResponse();
                    resp.setId(e.getId());
                    resp.setAmount(e.getAmount());
                    resp.setDescription(e.getDescription());
                    resp.setExpenseDate(e.getExpenseDate());
                    resp.setUserId(user.getId());
                    resp.setCategoryId(e.getCategory().getId());
                    return resp;
                }).collect(Collectors.toList());

        log.info("Fetched {} expenses for userId={}", list.size(), userId);

        return ResponseEntity.ok(list);
    }
}