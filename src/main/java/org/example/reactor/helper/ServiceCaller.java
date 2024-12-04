package org.example.reactor.helper;

import reactor.core.publisher.Mono;

public class ServiceCaller {

    // 模拟调用外部服务的方法
    public static Mono<String> callExternalService(String key) {
        // 这里只是一个示例，实际中你会有真正的服务调用逻辑
        // 假设服务调用可能会失败，我们随机模拟一个错误
        if (Math.random() > 0.5) {
            return Mono.error(new RuntimeException("Service call failed for key: " + key));
        } else {
            return Mono.just("Response from service for key: " + key);
        }
    }

    // 模拟从缓存获取数据的方法
    public static Mono<String> getFromCache(String key) {
        // 这里只是一个示例，实际中你会有真正的缓存逻辑
        // 假设缓存中总是有数据
        return Mono.just("Response from cache for key: " + key);
    }
}
