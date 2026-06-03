package com.rakesh.expensetracker.service.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rakesh.expensetracker.budget.dto.BudgetRequest;
import com.rakesh.expensetracker.budget.dto.BudgetResponse;
import com.rakesh.expensetracker.budget.entity.Budget;
import com.rakesh.expensetracker.budget.entity.BudgetType;
import com.rakesh.expensetracker.budget.repository.BudgetRepository;
import com.rakesh.expensetracker.budget.service.impl.BudgetServiceImpl;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.exception.ResourceNotFoundException;
import com.rakesh.expensetracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;
    
    @Test
    void testCreateBudgetSuccess() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        BudgetRequest request = new BudgetRequest();
        request.setBudgetType(BudgetType.CUSTOM);
        request.setTotalBudget(5000.0);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(30));

        Budget savedBudget = new Budget();
        savedBudget.setId(1L);
        savedBudget.setUser(user);
        savedBudget.setBudgetType(BudgetType.CUSTOM);
        savedBudget.setTotalBudget(5000.0);
        savedBudget.setWarningThreshold(80.0);
        savedBudget.setCriticalThreshold(100.0);
        savedBudget.setStartDate(request.getStartDate());
        savedBudget.setEndDate(request.getEndDate());

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(budgetRepository.save(any(Budget.class)))
                .thenReturn(savedBudget);

        BudgetResponse response =
                budgetService.createBudget(
                        "test@gmail.com",
                        request
                );

        assertEquals(
                5000.0,
                response.getTotalBudget()
        );

        assertEquals(
                BudgetType.CUSTOM,
                response.getBudgetType()
        );

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(budgetRepository)
                .save(any(Budget.class));
    }
    
    @Test
    void testCreateBudget_UserNotFound() {

        BudgetRequest request =
                new BudgetRequest();

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> budgetService.createBudget(
                        "test@gmail.com",
                        request
                )
        );
    }
    
    @Test
    void testGetUserBudgets() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        Budget budget = new Budget();
        budget.setId(1L);
        budget.setUser(user);
        budget.setBudgetType(BudgetType.CUSTOM);
        budget.setTotalBudget(5000.0);
        budget.setWarningThreshold(80.0);
        budget.setCriticalThreshold(100.0);
        budget.setStartDate(LocalDate.now());
        budget.setEndDate(LocalDate.now().plusDays(30));

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(budgetRepository.findByUser(user))
                .thenReturn(List.of(budget));

        List<BudgetResponse> responses =
                budgetService.getUserBudgets(
                        "test@gmail.com"
                );

        assertEquals(1, responses.size());

        assertEquals(
                5000.0,
                responses.get(0).getTotalBudget()
        );

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(budgetRepository)
                .findByUser(user);
    }
    
    @Test
    void testCreateBudget_InvalidWeeklyRange() {

        User user = new User();
        user.setId(1L);

        BudgetRequest request = new BudgetRequest();
        request.setBudgetType(BudgetType.WEEKLY);
        request.setTotalBudget(5000.0);

        request.setStartDate(
                LocalDate.of(2026, 6, 1)
        );

        request.setEndDate(
                LocalDate.of(2026, 6, 5)
        ); // invalid

        when(userRepository.findByEmail(
                "test@gmail.com"))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> budgetService.createBudget(
                                "test@gmail.com",
                                request
                        )
                );

        assertEquals(
                "Weekly budget must have exactly 7 days range",
                exception.getMessage()
        );
    }
   
}


