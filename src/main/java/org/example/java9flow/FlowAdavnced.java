package org.example.java9flow;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class FlowAdavnced {
    public static void main(String[] args) {
        SampleSubscriber<Boolean> subscriber = new SampleSubscriber<>(o -> {
            System.out.println("hello ....." + o);
        });
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try (SubmissionPublisher<Boolean> submissionPublisher = new SubmissionPublisher<>(executor, Flow.defaultBufferSize())) {
            submissionPublisher.subscribe(subscriber);
            for (int i = 0; i < 1000; i++) {
                System.out.println("发布第" + i + "条消息开始");
                // 为什么到第257条被阻塞住了, 那是因为缓冲区满了, 缓冲区出现空闲才会被允许接着生产。
                submissionPublisher.submit(true);
                System.out.println("发布第" + i + "条消息完毕");
            }
        }
        executor.shutdown();
    }

    static class SampleSubscriber<T> implements Flow.Subscriber<T> {
        final Consumer<? super T> consumer;
        Flow.Subscription subscription;

        SampleSubscriber(Consumer<? super T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("建立订阅关系");
            this.subscription = subscription; // 赋值
            subscription.request(1);
        }

        public void onNext(T item) {
            try {
                System.out.println("thread name 0" + Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("收到发送者的消息" + item);
            consumer.accept(item);
            // 可调用 subscription.request 接着请求发布者发消息
            subscription.request(1);
        }

        public void onError(Throwable ex) {
            System.out.println("onError");
            ex.printStackTrace();
        }

        public void onComplete() {
            System.out.println("onComplete");
        }
    }
}
