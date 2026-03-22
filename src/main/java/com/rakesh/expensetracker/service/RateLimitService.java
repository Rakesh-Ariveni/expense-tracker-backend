package com.rakesh.expensetracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int maxTokens, int refillRatePerSec) {

        String tokensKey = "rate_limit:tokens:" + key;
        String timestampKey = "rate_limit:timestamp:" + key;

        long now = System.currentTimeMillis();

        String lastRefillStr = redisTemplate.opsForValue().get(timestampKey);
        long lastRefill = (lastRefillStr == null) ? now : Long.parseLong(lastRefillStr);

        String tokensStr = redisTemplate.opsForValue().get(tokensKey);
        long tokens = (tokensStr == null) ? maxTokens : Long.parseLong(tokensStr);

        // 🔥 Refill logic
        long elapsedSeconds = (now - lastRefill) / 1000;
        long refill = elapsedSeconds * refillRatePerSec;

        if (refill > 0) {
            tokens = Math.min(maxTokens, tokens + refill);
            lastRefill = now;

            log.debug("Refilling tokens for key={} newTokens={}", key, tokens);
        }

        if (tokens > 0) {
            tokens--;

            redisTemplate.opsForValue().set(tokensKey, String.valueOf(tokens), Duration.ofMinutes(10));
            redisTemplate.opsForValue().set(timestampKey, String.valueOf(lastRefill), Duration.ofMinutes(10));

            log.debug("Request allowed key={} remainingTokens={}", key, tokens);

            return true;
        }

        log.warn("Rate limit exceeded for key={}", key);

        return false;
    }
}