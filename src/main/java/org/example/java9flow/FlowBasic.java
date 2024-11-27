package org.example.java9flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class FlowBasic {
    public static void main(String[] args) {
        SampleSubscriber<Integer> subscriber = new SampleSubscriber<>( o -> {
            System.out.println("hello ....." + o);
        });
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try (SubmissionPublisher<Integer> submissionPublisher = new SubmissionPublisher<>(executor, Flow.defaultBufferSize())) {
            submissionPublisher.subscribe(subscriber);
            submissionPublisher.submit(0);
            submissionPublisher.submit(1);
            submissionPublisher.submit(2);
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
            subscription.request(2);
        }

        public void onNext(T item) {
            System.out.println("收到发送者的消息" + item);
            consumer.accept(item);
            // 可调用 subscription.request 接着请求发布者发消息
            //  subscription.request(1);
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
