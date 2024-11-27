package org.example;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class GettingStart {
    public static void main(String[] args){
//        Flowable.fromArray(new String[]{"jack", "john"}).subscribe(s->
//                System.out.println("Hello " + s +"!" ));
//        creatingObservablesByJustOrFrom();
//        creatingObservablesByCreate();
//        creatingObservablesAsync();
        simpleComposition();
    }

    public static void creatingObservablesByJustOrFrom(){
        Observable.just("hello", "world").subscribe(System.out::println);
        Observable<String> o =  Observable.fromArray("a", "b", "c");
        o.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("onSubscribe, isDisposed: "+d.isDisposed());
            }

            @Override
            public void onNext(@NonNull String s) {
                System.out.println("onNext "+s);

            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("onError "+e);

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete ");

            }
        });
    }

    public static void creatingObservablesByCreate(){
        var observable = Observable.create(aSubscriber-> {
            for(int i=0; i< 50; i++){
                if(!aSubscriber.isDisposed()){
                    aSubscriber.onNext("value_%d".formatted(i));
                }
            }
            // after sending all values we complete the sequence
            if (!aSubscriber.isDisposed()) {
                aSubscriber.onComplete();
            }
        });
        observable.subscribe(System.out::println);
    }


    public static void creatingObservablesAsync(){
        var customObservableNonBlocking = Observable.create(aSubscriber-> {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i< 50; i++){
                        if(!aSubscriber.isDisposed()){
                            aSubscriber.onNext("value_%d".formatted(i));
                        }
                    }
                    // after sending all values we complete the sequence
                    if (!aSubscriber.isDisposed()) {
                        aSubscriber.onComplete();
                    }
                }
            });
            t.start();
        });
        customObservableNonBlocking.subscribe(System.out::println);
    }

    public static void simpleComposition(){
        var customObservableNonBlocking = Observable.create(aSubscriber-> {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i< 75; i++){
                        if(!aSubscriber.isDisposed()){
                            aSubscriber.onNext("value_%d".formatted(i));
                        }
                    }
                    // after sending all values we complete the sequence
                    if (!aSubscriber.isDisposed()) {
                        aSubscriber.onComplete();
                    }
                }
            });
            t.start();
        });
        customObservableNonBlocking.skip(10).take(5)
                .map(stringValue-> stringValue+"_xform")
                .subscribe(item-> System.out.println("On Next"+item));
    }
}
