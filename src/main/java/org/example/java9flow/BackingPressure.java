package org.example.java9flow;

import io.reactivex.rxjava3.core.BackpressureOverflowStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.DefaultSubscriber;

import java.util.concurrent.TimeUnit;

/**
 * https://medium.com/@srinuraop/rxjava-backpressure-3376130e76c1
 */
public class BackingPressure {
    public static void main(String[] args){
//        sample();
//        collectingItemsByWindow();
        flowable();
    }


    public static void flowable(){
        Flowable<Integer> flowable = Flowable
                .range(1, 1000)
                .onBackpressureBuffer(100, // 设置缓冲区大小
                        () -> System.out.println("Buffer overflowed"),
                        BackpressureOverflowStrategy.DROP_OLDEST) // 策略：丢弃最旧的数据
                .observeOn(Schedulers.io());

        flowable.subscribe(
                item -> {
                    // 模拟慢处理
                    Thread.sleep(10);
                    System.out.println("Processed: " + item);
                },
                Throwable::printStackTrace,
                () -> System.out.println("Completed")
        );

        // 等待处理完成
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sample(){
        Observable<Integer> observable = Observable.range(1, 50000).sample(1, TimeUnit.NANOSECONDS);
        observable.subscribe(s -> System.out.println("value after every 1 nano secs "+s));
    }

    public static void collectingItemsByWindow(){
        Observable<Observable<Integer>> observable = Observable.range(1, 133)
                .window(3);
        observable.subscribe(s-> {
            System.out.println("next window");
            s.subscribe(i-> System.out.println("items in window "+i));
        });

    }
}
