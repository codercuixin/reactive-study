package org.example.reactor;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class PublishVsSubscribeExample {
    public static void main(String[] args){
        subscribeOn();
        publishOn();
    }
    public static void subscribeOn() {
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

    public static void publishOn() {
        Flux.range(1, 5)
                .doOnNext(i -> System.out.println("Initial processing " + i + " on thread: " + Thread.currentThread().getName()))
                .publishOn(Schedulers.parallel()) // Change the thread for downstream processing
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
