package org.example.reactor;

import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;
import reactor.core.Disposable;

import java.time.Duration;
import java.util.concurrent.*;

public class SinksExample {

    public static void main(String[] args) {
        // 创建一个可以重播的Sink
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        // 创建一个Flux，用于订阅Sink
        Flux<Integer> flux = Flux.from(replaySink.asFlux());

        // 订阅Flux，打印接收到的数据
        Disposable subscription = flux.subscribe(System.out::println, Throwable::printStackTrace);

        // 线程1
        new Thread(() -> {
            replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        }).start();

        // 线程2
        new Thread(() -> {
            try {
                Thread.sleep(100); // 确保线程1先执行
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        }).start();

        // 线程3
        new Thread(() -> {
            try {
                Thread.sleep(200); // 确保线程2先执行
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 尝试发送数据，如果2秒内失败，则抛出EmissionException
            replaySink.emitNext(3, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(2)));
        }).start();

        // 线程4
        new Thread(() -> {
            try {
                Thread.sleep(300); // 确保线程3先执行
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 尝试发送数据，如果失败则返回FAIL_NON_SERIALIZED
            Sinks.EmitResult result = replaySink.tryEmitNext(4);
            if (result == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
                System.out.println("Failed to emit 4 due to non-serialized access");
            }
        }).start();

        // 等待一段时间后取消订阅，防止程序一直运行
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        subscription.dispose();
    }
}
