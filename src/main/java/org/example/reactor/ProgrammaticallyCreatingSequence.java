package org.example.reactor;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProgrammaticallyCreatingSequence {
    public static void main(String[] args) {
//        stateBasedGen();
//        mutableStateBasedGen();
//        fluxCreate();
        handle();
    }

    public static void stateBasedGen() {
        Flux.generate(
                        () -> 0,
                        (state, sink) -> {
                            sink.next("3 x " + state + " = " + 3 * state);
                            if (state == 10) {
                                sink.complete();
                            }
                            return state + 1;
                        })
                .subscribe(System.out::println);
    }

    public static void mutableStateBasedGen() {
        Flux.generate(
                        AtomicInteger::new,
                        (state, sink) -> {
                            long i = state.getAndIncrement();
                            sink.next("3 x " + state + " = " + 3 * i);
                            if (i == 10) {
                                sink.complete();
                            }
                            return state;
                        },
                        //in the case of the state containing a database connection or
                        // other resource that needs to be handled at the end of the process,
                        // the Consumer lambda could close the connection or otherwise handle any tasks that should be done at the end of the process.
                        (state) -> System.out.println(state))
                .subscribe(System.out::println);
    }

    public static void fluxCreate() {
        MyEventProcessor myEventProcessor = new MyEventProcessor();
        Flux.create(sink -> {
                    myEventProcessor.register(new MyEventListener<String>(

                    ) {
                        @Override
                        public void onDataChunk(List<String> chunk) {
                            for (String s : chunk) {
                                sink.next(s);
                            }
                        }

                        @Override
                        public void processComplete() {
                            sink.complete();
                        }
                    });
                })
           .subscribe(System.out::println);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        myEventProcessor.receiveDataChunk(List.of("hello", "world"));
        myEventProcessor.processComplete();
    }

    public static void handle(){
        Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null)
                        sink.next(letter);
                });

        alphabet.subscribe(System.out::println);
    }

    public static String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }

    interface MyEventListener<T> {
        void onDataChunk(List<T> chunk);

        void processComplete();
    }

    public static class MyEventProcessor {
        private MyEventListener<String> listener;

        // 假设有一个方法来注册事件监听器
        public void register(MyEventListener<String> listener) {
            // 存储监听器引用，以便在接收到数据时通知它
            this.listener = listener;
        }

        // 假设有一个方法来模拟接收数据块
        public void receiveDataChunk(List<String> chunk) {
            if (listener != null) {
                listener.onDataChunk(chunk);
            }
        }

        // 假设有一个方法来模拟处理完成
        public void processComplete() {
            if (listener != null) {
                listener.processComplete();
            }
        }
    }
}
