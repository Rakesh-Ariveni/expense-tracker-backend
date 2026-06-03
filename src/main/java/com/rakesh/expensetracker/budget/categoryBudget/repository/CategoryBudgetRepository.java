package com.rakesh.expensetracker.budget.categoryBudget.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rakesh.expensetracker.budget.categoryBudget.entity.categoryBudget;
import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.entity.Category;

public interface CategoryBudgetRepository
        extends JpaRepository<categoryBudget, Long> {

    List<categoryBudget> findByBudget(
            Budget budget
    );

    List<categoryBudget> findByCategory(
            Category category
    );

    categoryBudget findByBudgetAndCategory(
            Budget budget,
            Category category
    );
    
    @Query("""
 	       SELECT cb
 	       FROM categoryBudget cb
 	       JOIN FETCH cb.category
 	       WHERE cb.budget = :budget
 	       """)
 	List<categoryBudget> findByBudgetWithCategory(
 	        @Param("budget") Budget budget
 	);
}