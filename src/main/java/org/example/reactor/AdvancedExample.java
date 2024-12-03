package org.example.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class AdvancedExample {

    public static void main(String[] args) {
        // Example of buffer operator
//        bufferExample();

//        // Example of prefetch with flatMap
//        prefetchExample();
//
//        // Example of limitRate operator
        limitRateExample();
//
//        // Example of limitRequest operator
//        limitRequestExample();
    }

    private static void bufferExample() {
        System.out.println("Buffer Example:");
        Flux.range(1, 10)
            .buffer(3) // Buffer size of 3
            .subscribe(System.out::println); // Output: [1, 2, 3], [4, 5, 6], [7, 8, 9], [10]
    }

    private static void prefetchExample() {
        System.out.println("\nPrefetch Example:");
        Flux.range(1, 10)
            .flatMap(item -> Mono.just(item).delayElement(Duration.ofMillis(100)), 4) // Prefetch of 4
            .subscribe(System.out::println);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void limitRateExample() {
        System.out.println("\nLimit Rate Example:");
        Flux.range(1, 100)
            .limitRate(10) // Limit to batches of 10
            .subscribe(System.out::println); // Outputs 10 numbers at a time
    }

    private static void limitRequestExample() {
        System.out.println("\nLimit Request Example:");
        Flux.range(1, 100)
            .limitRequest(50) // Limit total demand to 50
            .subscribe(System.out::println); // Outputs numbers from 1 to 50
    }
}
