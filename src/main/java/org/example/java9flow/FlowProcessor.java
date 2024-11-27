package org.example.java9flow;

import java.util.concurrent.*;
import java.util.function.Consumer;

class FlowProcessor {
    public static void main(String[] args) throws Exception {
        SampleSubscriber<Boolean> subscriber = new SampleSubscriber<>(o -> {
            System.out.println("hello ....." + o);
        });
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try (SubmissionPublisher<Boolean> submissionPublisher = new SubmissionPublisher<>(executor, Flow.defaultBufferSize())) {
            MyProcessor myProcessor = new MyProcessor();
            // 做信息转发
            submissionPublisher.subscribe(myProcessor);
            myProcessor.subscribe(subscriber);
            for (int i = 0; i < 2; i++) {
                System.out.println("开始发布第" + i + "条消息");
                submissionPublisher.submit(true);
                System.out.println("开始发布第" + i + "条消息发布完毕");
            }
        }
        TimeUnit.SECONDS.sleep(2);
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

    static class MyProcessor extends SubmissionPublisher<Boolean> implements Flow.Processor<Boolean, Boolean> {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(Boolean item) {
            if (item) {
                item = false;
                // 处理器将此条信息转发
                this.submit(item);
                System.out.println("将true 转换为false");
            }
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            this.subscription.cancel();
        }

        @Override
        public void onComplete() {
            System.out.println("处理器处理完毕");
            this.close();
        }
    }
}
