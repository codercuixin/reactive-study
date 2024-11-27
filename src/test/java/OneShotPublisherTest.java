import org.example.OneShotPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

import static org.mockito.Mockito.*;

public class OneShotPublisherTest {

    @Test
    public void testSingleSubscription() {
        OneShotPublisher publisher = new OneShotPublisher();
        TestSubscriber subscriber1 = new TestSubscriber();
        TestSubscriber subscriber2 = new TestSubscriber();

        publisher.subscribe(subscriber1);
        Assertions.assertTrue(subscriber1.isSubscribed());

        publisher.subscribe(subscriber2);
        Assertions.assertTrue(subscriber2.getError() instanceof IllegalStateException);
    }

    // Helper class to simulate a Subscriber for testing
    private static class TestSubscriber implements Flow.Subscriber<Boolean> {
        private boolean subscribed;
        private boolean completed;
        private boolean receivedValue;
        private Throwable error;
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscribed = true;
            subscription.request(1); // Request one item
        }

        @Override
        public void onNext(Boolean item) {
            receivedValue = item;
            latch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            error = t;
            latch.countDown();
        }

        @Override
        public void onComplete() {
            completed = true;
            latch.countDown();
        }

        public boolean isSubscribed() {
            return subscribed;
        }

        public boolean isCompleted() {
            return completed;
        }

        public boolean getReceivedValue() {
            return receivedValue;
        }

        public Throwable getError() {
            return error;
        }

        public boolean isReceivedValue() {
            return receivedValue;
        }

        public void await() throws InterruptedException {
            latch.await();
        }

        public void cancel() {
            // Simulate cancelling the subscription
            // This should ideally call cancel on the actual subscription
            subscribed = false;
        }
    }
}
