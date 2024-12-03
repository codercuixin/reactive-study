package org.example.reactor;

import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class SimpleWayCreateFluxAndMono {

    public static void main(String[] args) throws InterruptedException {
//        factoryMethod();
//        subscribeMethods();
//        subscribeErrorSignal();
//        subscribeErrorAndCompletionSignal();
//        testDisposableSwap();
        testDisposableComposite();
    }


    public static void factoryMethod() {
        Flux<String> seq1 = Flux.just("foo", "bar", "foobar");
        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
        Flux<String> seq2 = Flux.fromIterable(iterable);

        Mono<String> noData = Mono.empty();
        Mono<String> data = Mono.just("foo");
        Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);
    }

    public static void subscribeMethods() {
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe();
        ints.subscribe(i -> System.out.println(i));
    }

    public static void subscribeErrorSignal() {
        Flux<Integer> ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) {
                        return i;
                    }
                    throw new RuntimeException("Got to 4");
                });
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error:" + error));
    }

    public static void subscribeErrorAndCompletionSignal() {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error:" + error),
                () -> System.out.println("Done"));
    }


    public static void testDisposableSwap() throws InterruptedException {
        // 创建一个可替换的 Disposable
        Disposable.Swap swap = Disposables.swap();

        // 模拟用户点击按钮，发起请求
        requestData(swap, 1);

        // 等待一段时间后再次点击按钮，替换请求
        Thread.sleep(1000);
        requestData(swap, 2);

        // 等待一段时间后，结束程序
        Thread.sleep(1500);
        swap.dispose(); // 释放资源
        System.out.println("All requests disposed.");
    }

    public static void testDisposableComposite() throws InterruptedException {
        // 创建一个 Composite Disposable
        Disposable compositeDisposable = Disposables.composite();

        // 发起多个请求并将其添加到 composite
        for (int i = 0; i < 3; i++) {
            Disposable disposable = requestData(i);
            compositeDisposable = Disposables.composite(compositeDisposable, disposable);
        }

        // 等待一段时间后，取消所有请求
        Thread.sleep(2000);
        compositeDisposable.dispose();
        System.out.println("All requests disposed.");
    }

    private static Disposable requestData(int index) {
        // 创建一个 Flux，模拟数据请求
        return Flux.interval(Duration.ofMillis(500))
                .take(5)
                .doOnNext(item -> System.out.println("Request " + index + ": Received item: " + item))
                .subscribe();
    }



    private static void requestData(Disposable.Swap swap, int requestNo) {
        // 创建一个 Flux，模拟数据请求
        Disposable disposable = Flux.interval(Duration.ofMillis(500))
                .take(10)
                .doOnNext(item -> System.out.println("from request:" + requestNo+", Received item: " + item))
                .subscribe();
        //观察下面这两个的区别
        // Atomically set the next {@link Disposable} on this container and dispose the previous one (if any)
        swap.update(disposable);
        //Atomically set the next Disposable on this container but don't dispose the previous one (if any).
//        swap.replace(disposable);
    }



}
