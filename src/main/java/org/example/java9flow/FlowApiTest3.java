package org.example.java9flow;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class FlowApiTest3 {
    static class MyProcessor extends SubmissionPublisher<String>
            implements Flow.Processor<String, String> {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            // 通过 Subscription 和发布者保持订阅关系，并用它来给发布者反馈
            this.subscription = subscription;

            // 请求一个数据
            this.subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            // 接收发布者发布的消息
            System.out.println("【处理器】接收消息 <------ " + item);

            // 处理器将消息进行转换
            String newItem = "【处理器加工后的数据: " + item + "】";

            this.submit(newItem);

            // 接收后再次请求一个数据，表示我已经处理完了，你可以再发数据过来了
            this.subscription.request(1);

            // 如果不想再接收数据，也可以直接调用cancel，表示不再接收了
            // this.subscription.cancel();
        }

        @Override
        public void onError(Throwable throwable) {
            // 过程中出现异常会回调这个方法
            System.out.println("【处理器】数据接收出现异常，" + throwable);

            // 出现异常，取消订阅，告诉发布者我不再接收数据了
            this.subscription.cancel();
        }

        @Override
        public void onComplete() {
            System.out.println("【处理器】数据处理完毕");

            // 处理器处理完数据后关闭
            this.close();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 1. 定义String类型的数据发布者，JDK 9自带的
        // SubmissionPublisher实现了 Publisher
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // 2. 创建处理器，用于接收发布者发布的消息，
        // 转换后再发送给订阅者
        MyProcessor processor = new MyProcessor();

        // 3. 发布者和处理器建立订阅的关系
        publisher.subscribe(processor);

        // 4.创建一个订阅者，用于接收处理器的消息
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(String item) {
                System.out.println("【订阅者】接收消息 <------ " + item + "");
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("【订阅者】数据接收出现异常，" + throwable);
                this.subscription.cancel();
            }

            @Override
            public void onComplete() {
                System.out.println("【订阅者】数据接收完毕");
            }
        };

        // 5. 处理器和订阅者建立订阅关系
        processor.subscribe(subscriber);

        // 6. 发布者开始发布数据
        for (int i = 0; i < 10; i++) {
            String message = "hello flow api " + i;
            System.out.println("【发布者】发布消息 ------> " + message);
            publisher.submit(message);
        }

        // 7. 发布结束后，关闭发布者
        publisher.close();

        // main线程延迟关闭，不然订阅者还没接收完消息，线程就被关闭了
        Thread.currentThread().join(2000);
    }
}
