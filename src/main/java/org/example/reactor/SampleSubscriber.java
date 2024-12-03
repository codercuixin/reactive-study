package org.example.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class SampleSubscriber<T> extends BaseSubscriber<T> {

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    @Override
    protected void hookOnNext(T value) {
        System.out.println(value);
        if(value instanceof Integer){
            if((Integer)value >=3){
                cancel();
                return;
            }
        }
        request(1);
    }

    @Override
    protected void hookOnCancel() {
        System.out.println("Canceled");
    }

    public static void main(String[] args){
        SampleSubscriber<Integer> ss = new SampleSubscriber<>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(ss);
    }
}
