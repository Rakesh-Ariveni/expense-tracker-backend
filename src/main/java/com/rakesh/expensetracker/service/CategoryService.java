package com.rakesh.expensetracker.service;

import com.rakesh.expensetracker.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category addCategory(Category category);

    Optional<Category> getCategoryByName(String name);

    List<Category> getAllCategories();
}
