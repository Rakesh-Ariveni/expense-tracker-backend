package com.rakesh.expensetracker.service.analytics;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log =
            LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private final StringRedisTemplate redisTemplate;

    public AnalyticsServiceImpl(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void updateAnalytics(
            ExpenseCreatedEvent event
    ) {

        Long userId = event.getUserId();

        Double amount = event.getAmount();

        String category = event.getCategory();

        // =========================
        // 1. TOTAL EXPENSES
        // =========================

        String totalKey =
                "analytics:user:" + userId + ":total";

        redisTemplate.opsForValue().increment(
                totalKey,
                amount
        );

        // =========================
        // 2. CATEGORY TOTALS
        // =========================

        String categoryKey =
                "analytics:user:" + userId + ":categories";

        redisTemplate.opsForHash().increment(
                categoryKey,
                category,
                amount
        );

        // =========================
        // 3. EXPENSE COUNT
        // =========================

        String countKey =
                "analytics:user:" + userId + ":count";

        redisTemplate.opsForValue().increment(
                countKey
        );

        log.info(
                "Analytics updated successfully for userId={}",
                userId
        );
    }

    @Override
    public Double getTotalExpenses(Long userId) {

        String totalKey =
                "analytics:user:" + userId + ":total";

        String value =
                redisTemplate.opsForValue().get(totalKey);

        if (value == null) {
            return 0.0;
        }

        return Double.valueOf(value);
    }

    @Override
    public Long getExpenseCount(Long userId) {

        String countKey =
                "analytics:user:" + userId + ":count";

        String value =
                redisTemplate.opsForValue().get(countKey);

        if (value == null) {
            return 0L;
        }

        return Long.valueOf(value);
    }

    @Override
    public Map<Object, Object> getCategoryAnalytics(Long userId) {

        String categoryKey =
                "analytics:user:" + userId + ":categories";

        Map<Object, Object> analytics =
                redisTemplate.opsForHash().entries(categoryKey);

        if (analytics == null) {
            return Collections.emptyMap();
        }

        return analytics;
    }
    
    @Override
    public void clearAnalytics(Long userId) {

        redisTemplate.delete(
                "analytics:user:" + userId + ":total"
        );

        redisTemplate.delete(
                "analytics:user:" + userId + ":count"
        );

        redisTemplate.delete(
                "analytics:user:" + userId + ":categories"
        );
    }
    
    @Override
    public Double getCategoryTotal(
            Long userId,
            String category
    ) {

        String categoryKey =
                "analytics:user:"
                        + userId
                        + ":categories";

        Object value =
                redisTemplate.opsForHash()
                        .get(categoryKey, category);

        if (value == null) {
            return 0.0;
        }

        return Double.valueOf(
                value.toString()
        );
    }
}