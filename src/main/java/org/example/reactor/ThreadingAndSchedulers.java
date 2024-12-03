package org.example.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class ThreadingAndSchedulers {


    public static void main(String[] args) throws InterruptedException {
//        startDemo();
//        publishOnExample();
        subscribeOnExample();
    }

    public static void startDemo() throws InterruptedException {
        final Mono<String> mono = Mono.just("hello ");
        Thread t = new Thread(() ->
                mono.map(msg -> msg + " thread ")

                        .subscribe(v -> System.out.println(v + Thread.currentThread().getName()))
        );
        t.start();
        t.join();
    }


    public static void publishOnExample() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        final Flux<String> flux = Flux.range(1, 2)
                .map(i -> {
                    System.out.printf("10+i map run on thread:%s%n", Thread.currentThread().getName());
                    return 10 + i;
                })
                .publishOn(s)
                .map(i -> {
                    System.out.printf("value+i map run on thread:%s%n", Thread.currentThread().getName());
                    return  "Value " + i;
                });
        new Thread(() -> flux.subscribe(System.out::println)).start();
    }

    public static void subscribeOnExample() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        final Flux<String> flux = Flux.range(1, 2)
                .map(i -> {
                    System.out.printf("10+i map run on thread:%s%n", Thread.currentThread().getName());
                    return 10 + i;
                })
                .subscribeOn(s)
                .map(i -> {
                    System.out.printf("value+i map run on thread:%s%n", Thread.currentThread().getName());
                    return  "Value " + i;
                });
        new Thread(() -> flux.subscribe(System.out::println)).start();
    }
}
