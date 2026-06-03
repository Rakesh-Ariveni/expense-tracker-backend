package com.rakesh.expensetracker.test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // 🔥 CPU Load API
    @GetMapping("/test/cpu")
    public String cpuLoad() {
        long end = System.currentTimeMillis() + 20000; // 20 seconds
        while (System.currentTimeMillis() < end) {
            Math.sqrt(Math.random());
        }
        return "CPU Load Done";
    }

    // 🔥 Slow API
    @GetMapping("/test/slow")
    public String slowApi() throws InterruptedException {
        Thread.sleep(2000); // 2 seconds delay
        return "Slow response";
    }

    // 🔥 Error API
    @GetMapping("/test/error")
    public String errorApi() {
        throw new RuntimeException("Test error triggered");
    }
}