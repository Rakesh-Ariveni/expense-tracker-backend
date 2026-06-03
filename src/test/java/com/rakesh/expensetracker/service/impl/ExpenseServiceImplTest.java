package com.rakesh.expensetracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.kafka.producer.ExpenseEventProducer;
import com.rakesh.expensetracker.repository.ExpenseRepository;
import com.rakesh.expensetracker.repository.UserRepository;
import com.rakesh.expensetracker.service.AsyncService;
import com.rakesh.expensetracker.service.MonitoringService;


@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AsyncService asyncService;

    @Mock
    private MonitoringService monitoringService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;
    
    @Mock
    private ExpenseEventProducer expenseEventProducer;

    private Expense expense;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(500.0);
        expense.setDescription("Pizza");
        expense.setExpenseDate(LocalDateTime.now());
        expense.setUser(user);
        expense.setCategory(category);
    }

    @Test
    void testAddExpenseSuccess() {

        // MOCK repository save response
        when(expenseRepository.save(expense))
                .thenReturn(expense);

        // CALL actual service method
        Expense savedExpense = expenseService.addExpense(expense);

        // ASSERT result
        assertEquals(500.0, savedExpense.getAmount());
        assertEquals("Pizza", savedExpense.getDescription());

        // VERIFY interactions
        verify(expenseRepository).save(expense);

        verify(asyncService)
                .logExpenseCreation(expense.getId());

        verify(monitoringService)
                .incrementApi("add_expense");

        verify(monitoringService)
                .expenseCreated("Food");
        
        verify(expenseEventProducer)
        .publishExpenseCreatedEvent(any());
    }
    
    @Test
    void testUpdateExpense_NotFound() {

        // ARRANGE
        when(expenseRepository.existsById(1L))
                .thenReturn(false);

        // ACT + ASSERT
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> expenseService.updateExpense(expense)
                );

        // VERIFY MESSAGE
        assertEquals(
                "Expense not found: 1",
                exception.getMessage()
        );

        // VERIFY repository call happened
        verify(expenseRepository)
                .existsById(1L);
    }
    
    @Test
    void testGetExpenses() {

        // ARRANGE

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        Category category = new Category();
        category.setName("Food");

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setAmount(500.0);
        expense.setDescription("Pizza");
        expense.setExpenseDate(LocalDateTime.now());
        expense.setCategory(category);
        expense.setUser(user);

        Page<Expense> expensePage =
                new PageImpl<>(List.of(expense));

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.findByUser(
                eq(user),
                any(Pageable.class)
        )).thenReturn(expensePage);

        // ACT

        Page<ExpenseDTO> result =
                expenseService.getExpenses(
                        "test@gmail.com",
                        0,
                        5,
                        "amount",
                        "desc",
                        null,
                        null,
                        null
                );

        // ASSERT

        assertEquals(1, result.getTotalElements());

        ExpenseDTO dto = result.getContent().get(0);

        assertEquals("Pizza", dto.getDescription());
        assertEquals("Food", dto.getCategoryName());
        assertEquals(500.0, dto.getAmount());

        // VERIFY

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(expenseRepository)
                .findByUser(
                        eq(user),
                        any(Pageable.class)
                );
    }
}