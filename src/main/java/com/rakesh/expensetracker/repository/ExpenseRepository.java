package com.rakesh.expensetracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	// =========================
    // LISTING APIs (Pagination)
    // =========================
    Page<Expense> findByUser(User user, Pageable pageable);
    List<Expense> findByUser(User user);

    Page<Expense> findByUserAndCategory_Name(
            User user,
            String categoryName,
            Pageable pageable
    );

    Page<Expense> findByUserAndExpenseDateBetween(
            User user,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    // =========================
    // DASHBOARD QUERIES
    // =========================
	
    @Query("""
        SELECT e FROM Expense e
        WHERE e.user = :user
        ORDER BY e.expenseDate DESC
    """)
    List<Expense> findRecentExpenses(
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user = :user
    """)
    Double findTotalExpensesByUser(@Param("user") User user);

    @Query("""
        SELECT e.category.name, SUM(e.amount)
        FROM Expense e
        WHERE e.user = :user
        GROUP BY e.category.name
    """)
    List<Object[]> findExpensesByCategory(@Param("user") User user);

    @Query("""
    	    SELECT e.category.name
    	    FROM Expense e
    	    WHERE e.user = :user
    	    GROUP BY e.category.name
    	    ORDER BY SUM(e.amount) DESC
    	""")
    	List<String> findTopCategory(
    	        @Param("user") User user,
    	        Pageable pageable
    	);

    @Query("""
        SELECT DATE(e.expenseDate), SUM(e.amount)
        FROM Expense e
        WHERE e.user = :user
          AND e.expenseDate >= :startDate
        GROUP BY DATE(e.expenseDate)
        ORDER BY DATE(e.expenseDate)
    """)
    List<Object[]> findWeeklyTrend(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("""
    	    SELECT e.category.name
    	    FROM Expense e
    	    WHERE e.user = :user
    	    GROUP BY e.category.name
    	    ORDER BY COUNT(e.id) DESC
    	""")
    	List<String> findMostFrequentCategory(
    	        @Param("user") User user,
    	        Pageable pageable
    	);
}
