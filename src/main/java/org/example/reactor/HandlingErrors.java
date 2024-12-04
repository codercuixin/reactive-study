package org.example.reactor;

import org.example.reactor.helper.ServiceCaller;
import org.example.reactor.helper.Stats;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static org.example.reactor.helper.ServiceCaller.callExternalService;

public class HandlingErrors {
    public static void main(String[] args) throws InterruptedException {
//        String value = normalCatch();
//        System.out.println(value);

//        onErrorReturn();
//        onCustomErrorReturn();
//        onErrorComplete();
//        catchRethrown();
//        longOrReactOnSide();
//        doFinally();
//        intervalTriggerError();
//        simpleRetry();
//        retryWhen();
//        retryWhen2();

        handleError();
        wrapError();
    }

    public static void wrapError(){
        Flux<String> converted = Flux
                .range(1, 10)
                .map(i -> {
                    try { return convert(i); }
                    catch (IOException e) { throw Exceptions.propagate(e); }
                });
        converted.subscribe(
                v -> System.out.println("RECEIVED: " + v),
                e -> {
                    if (Exceptions.unwrap(e) instanceof IOException) {
                        System.out.println("Something bad happened with I/O");
                    } else {
                        System.out.println("Something bad happened");
                    }
                }
        );
    }
    public static String convert(int i) throws IOException {
        if (i > 3) {
            throw new IOException("boom " + i);
        }
        return "OK " + i;
    }

    public static void handleError(){
        Flux.just("foo")
                .map(s -> { throw new IllegalArgumentException(s); })
                .subscribe(v -> System.out.println("GOT VALUE"),
                        e -> System.out.println("ERROR: " + e));
    }

    public static void retryWhen2(){
        AtomicInteger errorCount = new AtomicInteger();
        Flux<String> flux =
                Flux.<String>error(new IllegalArgumentException())
                        .doOnError(e -> errorCount.incrementAndGet())
                        .retryWhen(Retry.from(companion ->
                                companion.map(rs -> {
                                    if (rs.totalRetries() < 3) return rs.totalRetries();
                                    else throw new RuntimeException(rs.failure());
                                })
                        ));
        flux.subscribe(System.out::println);
    }

    public static void retryWhen(){
        Flux<String> flux = Flux
                .<String>error(new IllegalArgumentException())
                .doOnError(System.out::println)
                //Here, we consider the first three errors as retry-able (take(3)) and then give up.
                .retryWhen(Retry.from(companion ->
                        companion.take(3)));
        flux.subscribe(System.out::println);
    }

    public static void simpleRetry() throws InterruptedException {
       Flux.interval(Duration.ofMillis(250))
                .map(input-> {
                    if(input < 3){
                        return "tick"+input;
                    }
                    throw new RuntimeException("boom");
                })
                .retry(1)
                .elapsed()
               .subscribe(System.out::println);
        Thread.sleep(2100);
    }
    public static void intervalTriggerError() throws InterruptedException {
        Flux<String> flux = Flux.interval(Duration.ofMillis(250))
                .map(input-> {
                    if(input < 3){
                        return "tick"+input;
                    }
                    throw new RuntimeException("boom");
                }).onErrorReturn("Uh oh");
        flux.subscribe(System.out::println);
        Thread.sleep(2100);
    }

    public static void doFinally() {
        Stats stats = new Stats();
        Flux.just("foo", "bar")
                .doOnSubscribe(s-> stats.startTimer())
                .doFinally(type -> {
                    stats.stopTimerAndRecordTiming();
                    long cost = stats.getDurationMillis();
                    System.out.printf("time cost:%d, finally type:%s\n", cost, type);
                })
                //	take(1) requests exactly 1 from upstream, and cancels after one item is emitted
                .take(1)
                .subscribe(System.out::println);
    }

    public static void longOrReactOnSide() {
        LongAdder failureStat = new LongAdder();
        Flux<String> flux =
                Flux.just("unknown")
                        .flatMap(k -> callExternalService(k)
                                .doOnError(e -> {
                                    failureStat.increment();
                                    System.out.println("uh oh, falling back, service failed for key " + k);
                                })

                        );
        flux.subscribe(System.out::println);
    }

    public static void catchRethrown() {
        Flux.just("timeout1")
                .flatMap(ServiceCaller::callExternalService)
                .onErrorMap(original -> new RuntimeException("oops, SLA exceeded", original))
                .subscribe(System.out::println);
    }

    //fallback method
    public static void onErrorResume() {
        Flux.just("key1", "key2")
                .flatMap(k -> callExternalService(k)
                        .onErrorResume(e -> ServiceCaller.getFromCache(k))
                ).subscribe(System.out::println);
    }


    // Catch and swallow the error
    public static void onErrorComplete() {
        Flux.just(30, 20, 10, 10)
                .map(HandlingErrors::doSomethingDangerous)
                .onErrorComplete()
                .subscribe(System.out::println);
    }

    //default value
    public static void onErrorReturn() {
        Flux.just(10).map(HandlingErrors::doSomethingDangerous)
                .onErrorReturn("RECOVERED")
                .subscribe(System.out::println);
    }

    //predicated error , default value
    public static void onCustomErrorReturn() {
        Flux.just(10).map(HandlingErrors::doSomethingDangerous)
                .onErrorReturn(e -> e.getMessage().equals("boom10"), "RECOVERED")
                .subscribe(System.out::println);
    }

    public static String normalCatch() {
        try {
            return doSomethingDangerous(10);
        } catch (Throwable error) {
            return "RECOVERED";
        }
    }

    public static String doSomethingDangerous(int number) {
        if (number != 10) {
            return String.valueOf(number);
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RuntimeException("boom10");
        } else {
            throw new RuntimeException("another boom");
        }
    }
}
