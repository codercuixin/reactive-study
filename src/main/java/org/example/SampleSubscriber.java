package org.example;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

class SampleSubscriber<T> implements Flow.Subscriber<T> {
    final Consumer<? super T> consumer;
    final long bufferSize;
    Flow.Subscription subscription;
    long count;

    SampleSubscriber(long bufferSize, Consumer<? super T> consumer) {
        this.bufferSize = bufferSize;
        this.consumer = consumer;
    }

    public static void main(String[] args) {
        SampleSubscriber subscriber = new SampleSubscriber<>(200L, o -> {
            System.out.println("hello ....." + o);
        });
        ExecutorService executor = Executors.newFixedThreadPool(1);
        SubmissionPublisher<Boolean> submissionPublisher = new SubmissionPublisher(executor, Flow.defaultBufferSize());
        submissionPublisher.subscribe(subscriber);
        submissionPublisher.submit(true);
        submissionPublisher.submit(true);
        submissionPublisher.submit(true);
        executor.shutdown();
    }

    public void onSubscribe(Flow.Subscription subscription) {
        long initialRequestSize = bufferSize;
        count = bufferSize - bufferSize / 2; // re-request when half consumed
        (this.subscription = subscription).request(initialRequestSize);
    }

    public void onNext(T item) {
        if (--count <= 0)
            subscription.request(count = bufferSize - bufferSize / 2);
        consumer.accept(item);
    }

    public void onError(Throwable ex) {
        ex.printStackTrace();
    }

    public void onComplete() {
    }

}
