package com.rakesh.expensetracker.service.impl;

import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.repository.CategoryRepository;
import com.rakesh.expensetracker.service.CategoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category addCategory(Category category) {

        log.info("Adding category name={}", category.getName());

        Category saved = categoryRepository.save(category);

        log.info("Category created successfully id={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {

        log.debug("Fetching category by name={}", name);

        return categoryRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {

        log.debug("Fetching all categories");

        return categoryRepository.findAll();
    }
}