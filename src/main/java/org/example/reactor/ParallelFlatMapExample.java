package org.example.reactor;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class ParallelFlatMapExample {
    public static void main(String[] args) {
        Flux<String> originalFlux = Flux.just("Item 1", "Item 2", "Item 3");

        // Using flatMap to process items in parallel
        Flux<String> parallelFlux = originalFlux.flatMap(item ->
            // Simulate an asynchronous operation
            Flux.just(item + " - Processed")
                .delayElements(Duration.ofMillis(ThreadLocalRandom.current().nextInt(500))) // Simulate delay
                .subscribeOn(Schedulers.boundedElastic()) // Specify the scheduler for this operation
        );

        // Subscribe to the new Flux and print the results
        parallelFlux.subscribe(result ->
            System.out.println("Result: " + result + " on thread: " + Thread.currentThread().getName())
        );

        // Add sleep to allow time for the async processing to complete
        try {
            Thread.sleep(2000); // Wait long enough for all items to be processed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
