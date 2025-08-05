package com.example.datadog_micrometer_demo;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class DemoController {

    private final Timer myServiceLatency;

    public DemoController(MeterRegistry registry) {
        this.myServiceLatency = Timer.builder("demo.service.latency")
                .description("Measures the latency of the /hello endpoint")
                .tag("endpoint", "hello")
                .register(registry);
    }

    @GetMapping("/hello")
    public String sayHello() {
        // Start the timer
        Timer.Sample sample = Timer.start();

        try {
            // Simulate a task that takes a random amount of time
            long randomSleep = (long) (Math.random() * 500);
            TimeUnit.MILLISECONDS.sleep(randomSleep);
            return "Hello, Micrometer!";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error!";
        } finally {
            // Stop the timer and record the duration
            sample.stop(myServiceLatency);
        }
    }
}
