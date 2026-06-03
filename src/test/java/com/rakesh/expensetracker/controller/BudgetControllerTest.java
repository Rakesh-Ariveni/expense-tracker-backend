package com.rakesh.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakesh.expensetracker.budget.controller.BudgetController;
import com.rakesh.expensetracker.budget.dto.BudgetRequest;
import com.rakesh.expensetracker.budget.dto.BudgetResponse;
import com.rakesh.expensetracker.budget.entity.BudgetType;
import com.rakesh.expensetracker.budget.service.BudgetService;
import com.rakesh.expensetracker.config.JwtAuthenticationFilter;
import com.rakesh.expensetracker.config.JwtService;
import com.rakesh.expensetracker.service.MonitoringService;

@WebMvcTest(BudgetController.class)
@AutoConfigureMockMvc(addFilters = false)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BudgetService budgetService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private MonitoringService monitoringService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private Authentication authentication;

    @Test
    void testCreateBudgetSuccess() throws Exception {

    	String email = authentication.getName();
        BudgetRequest request = new BudgetRequest();
        request.setTotalBudget(5000.0);
        request.setBudgetType(BudgetType.MONTHLY);
        request.setStartDate(
                LocalDate.now()
        );

        request.setEndDate(
                LocalDate.now().plusMonths(1)
        );

        BudgetResponse response =
                new BudgetResponse();

        when(budgetService.createBudget(
                anyString(),
                any(BudgetRequest.class)))
                .thenReturn(response);
        
        when(authentication.getName())
        .thenReturn("test@gmail.com");

        mockMvc.perform(
                post("/api/budgets")
                        .principal(authentication)
                        .contentType("application/json")
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void testGetBudgetsSuccess() throws Exception {

    	String email = authentication.getName();
        when(budgetService.getUserBudgets(anyString()))
                .thenReturn(List.of());
        
        when(authentication.getName())
        .thenReturn("test@gmail.com");

        mockMvc.perform(
                get("/api/budgets")
                        .principal(authentication)
        )
        .andExpect(status().isOk());
    }
}