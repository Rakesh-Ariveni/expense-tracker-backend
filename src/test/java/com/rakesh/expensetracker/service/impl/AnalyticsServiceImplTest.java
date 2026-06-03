package com.rakesh.expensetracker.service.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;
import com.rakesh.expensetracker.service.analytics.AnalyticsServiceImpl;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    void testUpdateAnalytics() {

        ExpenseCreatedEvent event =
                new ExpenseCreatedEvent();

        event.setUserId(1L);
        event.setAmount(500.0);
        event.setCategory("Food");

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        analyticsService.updateAnalytics(event);

        verify(valueOperations)
                .increment(
                        "analytics:user:1:total",
                        500.0
                );

        verify(hashOperations)
                .increment(
                        "analytics:user:1:categories",
                        "Food",
                        500.0
                );

        verify(valueOperations)
                .increment(
                        "analytics:user:1:count"
                );
    }

    @Test
    void testGetTotalExpenses() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(
                "analytics:user:1:total"))
                .thenReturn("1500.0");

        Double total =
                analyticsService.getTotalExpenses(1L);

        assertEquals(
                1500.0,
                total
        );
    }

    @Test
    void testGetExpenseCount() {

        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        when(valueOperations.get(
                "analytics:user:1:count"))
                .thenReturn("25");

        Long count =
                analyticsService.getExpenseCount(1L);

        assertEquals(
                25L,
                count
        );
    }

    @Test
    void testGetCategoryAnalytics() {

        Map<Object, Object> analytics =
                new HashMap<>();

        analytics.put(
                "Food",
                2000.0
        );

        analytics.put(
                "Travel",
                1500.0
        );

        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        when(hashOperations.entries(
                "analytics:user:1:categories"))
                .thenReturn(analytics);

        Map<Object, Object> result =
                analyticsService.getCategoryAnalytics(1L);

        assertEquals(
                2,
                result.size()
        );
    }

    @Test
    void testClearAnalytics() {

        analyticsService.clearAnalytics(1L);

        verify(redisTemplate)
                .delete(
                        "analytics:user:1:total"
                );

        verify(redisTemplate)
                .delete(
                        "analytics:user:1:count"
                );

        verify(redisTemplate)
                .delete(
                        "analytics:user:1:categories"
                );
    }
    
    @Test
    void testGetCategoryTotal() {

        when(redisTemplate.opsForHash())
                .thenReturn(hashOperations);

        when(hashOperations.get(
                "analytics:user:1:categories",
                "Food"))
                .thenReturn("2500.0");

        Double total =
                analyticsService.getCategoryTotal(
                        1L,
                        "Food"
                );

        assertEquals(
                2500.0,
                total
        );
    }
}