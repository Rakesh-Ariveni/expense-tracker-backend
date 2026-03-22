package com.rakesh.expensetracker.service;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class MonitoringService {

    private final MeterRegistry meterRegistry;

    public MonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // API metrics
    public void incrementApi(String apiName) {
        meterRegistry.counter("api.calls", "api", apiName).increment();
    }

    // Cache metrics
    public void cacheHit() {
        meterRegistry.counter("cache.hit").increment();
    }

    public void cacheMiss() {
        meterRegistry.counter("cache.miss").increment();
    }

    // Error metrics
    public void error() {
        meterRegistry.counter("errors.total").increment();
    }
}