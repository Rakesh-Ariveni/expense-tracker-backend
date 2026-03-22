package com.rakesh.expensetracker.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isDuplicate(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void saveKey(String key) {
        redisTemplate.opsForValue().set(key, "processed", Duration.ofMinutes(10));
    }
}
