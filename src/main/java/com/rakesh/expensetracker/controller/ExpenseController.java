package com.rakesh.expensetracker.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.dto.ExpenseRequest;
import com.rakesh.expensetracker.dto.ExpenseResponse;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.service.CategoryService;
import com.rakesh.expensetracker.service.ExpenseService;
import com.rakesh.expensetracker.service.UserService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final CategoryService categoryService;

    public ExpenseController(ExpenseService expenseService, UserService userService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@RequestBody ExpenseRequest req) {
        User user = userService.getUserById(req.getUserId());
        Category category = categoryService
                .getCategoryByName(req.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found"));

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
        String email = authentication.getName();
        return expenseService.getExpenses(
                email, page, size, sortBy, direction, category, from, to
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByUser(@PathVariable Long userId) {
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

        return ResponseEntity.ok(list);
    }
}
