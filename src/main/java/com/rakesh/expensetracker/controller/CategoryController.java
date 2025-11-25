package com.rakesh.expensetracker.controller;

import com.rakesh.expensetracker.dto.CategoryRequest;
import com.rakesh.expensetracker.dto.CategoryResponse;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@RequestBody CategoryRequest req) {
        Category category = new Category();
        category.setName(req.getName());

        Category saved = categoryService.addCategory(category);

        CategoryResponse resp = new CategoryResponse();
        resp.setId(saved.getId());
        resp.setName(saved.getName());

        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> list = categoryService.getAllCategories()
                .stream().map(c -> {
                    CategoryResponse resp = new CategoryResponse();
                    resp.setId(c.getId());
                    resp.setName(c.getName());
                    return resp;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}
