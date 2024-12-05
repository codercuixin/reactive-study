package org.example.reactor;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SubscribeOnExample {
    public static void main(String[] args) {
        Flux.range(1, 5)
            .subscribeOn(Schedulers.boundedElastic()) // Use a different thread for subscription
            .doOnNext(i -> System.out.println("Processing " + i + " on thread: " + Thread.currentThread().getName()))
            .subscribe();

        // Add sleep to allow time for the async processing to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
