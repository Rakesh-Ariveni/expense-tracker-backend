package com.rakesh.expensetracker.repository;

import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user); // fetch all expenses of a user
}
