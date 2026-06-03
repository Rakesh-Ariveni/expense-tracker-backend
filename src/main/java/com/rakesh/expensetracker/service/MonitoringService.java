package com.rakesh.expensetracker.service;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class MonitoringService {

    private final MeterRegistry meterRegistry;

    public MonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // =========================================
    // API METRICS
    // =========================================

    public void incrementApi(String apiName) {

        Counter.builder("api.calls")
                .tag("api", apiName)
                .register(meterRegistry)
                .increment();
    }

    // =========================================
    // BUSINESS METRICS
    // =========================================

    public void expenseCreated(String category) {

        Counter.builder("expenses.created")
                .tag("category", category)
                .register(meterRegistry)
                .increment();
    }

    public void expenseCreationFailed(String reason) {

        Counter.builder("expenses.failed")
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }

    // =========================================
    // TIMER METRICS
    // =========================================

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopExpenseCreationTimer(Timer.Sample sample) {

        sample.stop(
                Timer.builder("expenses.creation.duration")
                        .description("Time taken to create expense")
                        .register(meterRegistry)
        );
    }

    // =========================================
    // CACHE METRICS
    // =========================================

    public void cacheHit() {

        Counter.builder("cache.hit")
                .register(meterRegistry)
                .increment();
    }

    public void cacheMiss() {

        Counter.builder("cache.miss")
                .register(meterRegistry)
                .increment();
    }

    // =========================================
    // ERROR METRICS
    // =========================================

    public void error() {

        Counter.builder("errors.total")
                .register(meterRegistry)
                .increment();
    }
}