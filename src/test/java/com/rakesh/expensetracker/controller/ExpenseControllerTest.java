package com.rakesh.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakesh.expensetracker.config.JwtAuthenticationFilter;
import com.rakesh.expensetracker.config.JwtService;
import com.rakesh.expensetracker.dto.ExpenseDTO;
import com.rakesh.expensetracker.dto.ExpenseRequest;
import com.rakesh.expensetracker.entity.Category;
import com.rakesh.expensetracker.entity.Expense;
import com.rakesh.expensetracker.entity.User;
import com.rakesh.expensetracker.service.CategoryService;
import com.rakesh.expensetracker.service.ExpenseService;
import com.rakesh.expensetracker.service.IdempotencyService;
import com.rakesh.expensetracker.service.MonitoringService;
import com.rakesh.expensetracker.service.RateLimitService;
import com.rakesh.expensetracker.service.UserService;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private UserService userService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private IdempotencyService idempotencyService;

    @MockBean
    private RateLimitService rateLimitService;
    
    @MockBean
    private MonitoringService monitoringService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private Authentication authentication;

    @Test
    void testAddExpense() throws Exception {

        // USER
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        // CATEGORY
        Category category = new Category();
        category.setId(1L);
        category.setName("Food");

        // REQUEST
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(500.0);
        request.setDescription("Pizza");
        request.setExpenseDate(LocalDateTime.now());
        request.setUserId(1L);
        request.setCategoryName("Food");

        // SAVED EXPENSE
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setAmount(500.0);
        expense.setDescription("Pizza");
        expense.setExpenseDate(LocalDateTime.now());
        expense.setUser(user);
        expense.setCategory(category);

        // MOCKS
        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(rateLimitService.isAllowed(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(true);

        when(idempotencyService.isDuplicate(any()))
                .thenReturn(false);

        when(userService.getUserById(1L))
                .thenReturn(user);

        when(categoryService.getCategoryByName("Food"))
                .thenReturn(Optional.of(category));

        when(expenseService.addExpense(any(Expense.class)))
                .thenReturn(expense);

        // API CALL + ASSERTIONS
        mockMvc.perform(post("/api/expenses")
                .header("Idempotency-Key", "abc123")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500.0))
                .andExpect(jsonPath("$.description").value("Pizza"));
    }
    
    @Test
    void testAddExpense_MissingIdempotencyKey() throws Exception {

        // USER
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        // REQUEST
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(500.0);
        request.setDescription("Pizza");
        request.setExpenseDate(LocalDateTime.now());
        request.setUserId(1L);
        request.setCategoryName("Food");

        // MOCKS
        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(rateLimitService.isAllowed(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(true);

        // API CALL + ASSERTIONS
        mockMvc.perform(post("/api/expenses")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
    
    @Test
    void testAddExpense_DuplicateRequest() throws Exception {

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(500.0);
        request.setDescription("Pizza");
        request.setExpenseDate(LocalDateTime.now());
        request.setUserId(1L);
        request.setCategoryName("Food");

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(rateLimitService.isAllowed(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(true);

        when(idempotencyService.isDuplicate("abc123"))
                .thenReturn(true);

        mockMvc.perform(post("/api/expenses")
                .header("Idempotency-Key", "abc123")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
    
    @Test
    void testAddExpense_RateLimitExceeded() throws Exception {

        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(500.0);
        request.setDescription("Pizza");
        request.setExpenseDate(LocalDateTime.now());
        request.setUserId(1L);
        request.setCategoryName("Food");

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(rateLimitService.isAllowed(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(false);

        mockMvc.perform(post("/api/expenses")
                .header("Idempotency-Key", "abc123")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
    
    @Test
    void testGetExpensesByUser() throws Exception {

        User user = new User();
        user.setId(1L);

        Category category = new Category();
        category.setId(1L);

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setAmount(500.0);
        expense.setDescription("Pizza");
        expense.setExpenseDate(LocalDateTime.now());
        expense.setUser(user);
        expense.setCategory(category);

        when(userService.getUserById(1L))
                .thenReturn(user);

        when(expenseService.getExpensesByUser(user))
                .thenReturn(List.of(expense));

        mockMvc.perform(get("/api/expenses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(500.0))
                .andExpect(jsonPath("$[0].description").value("Pizza"));
    }
    
    @Test
    void testGetExpenses() throws Exception {

        ExpenseDTO dto = new ExpenseDTO();
        dto.setAmount(500.0);
        dto.setDescription("Pizza");

        Page<ExpenseDTO> page =
                new PageImpl<>(List.of(dto));

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(expenseService.getExpenses(
                any(),
                anyInt(),
                anyInt(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(page);

        mockMvc.perform(get("/api/expenses")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(500.0))
                .andExpect(jsonPath("$.content[0].description").value("Pizza"));
    }
}