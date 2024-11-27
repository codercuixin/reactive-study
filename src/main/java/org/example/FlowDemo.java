package org.example;

import java.util.concurrent.*;

public class FlowDemo {
    public static void main(String[] args) {
        SampleSubscriber<Integer> subscriber = new SampleSubscriber<>();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try (SubmissionPublisher<Integer> submissionPublisher = new SubmissionPublisher<>(executor, Flow.defaultBufferSize())) {
            submissionPublisher.subscribe(subscriber);
            for (int i = 0; i < 1000; i++) {
                System.out.println("发布第" + i + "条消息开始");
                submissionPublisher.submit(i);
                System.out.println("发布第" + i + "条消息结束");
            }
        }
        executor.shutdown();
    }

    static class SampleSubscriber<T> implements Flow.Subscriber<T> {
        Flow.Subscription subscription;

        SampleSubscriber() {
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("建立订阅关系");
            this.subscription = subscription; // 赋值
            //由于当前消费能力不足，调用者只向发布者要两条消息。
            subscription.request(2);
        }

        public void onNext(T item) {
            try {
                System.out.printf("thread name %s, 收到发送者的消息:%s\n".formatted(Thread.currentThread().getName(), item));
                TimeUnit.SECONDS.sleep(2);
                if((item instanceof Integer) && (Integer)item >= 10){
                    subscription.cancel();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 可调用 subscription.request 接着请求发布者发消息
            subscription.request(2);

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
