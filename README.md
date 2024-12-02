### reactive stream
reactive stream：asynchronous stream processing with non-blocking backpressure.

[官网](https://www.reactive-streams.org/)

[规范](https://github.com/reactive-streams/reactive-streams-jvm)
#### 核心概念
响应式流规范定义了如何处理非阻塞的异步流处理，使用非阻塞的背压机制。
它由四个关键接口组成：Publisher（发布者）、Subscriber（订阅者）、Subscription（订阅）和 Processor（处理器）。

1. **Publisher（发布者）**：
   - 发布者向订阅者发送数据。
   - 它负责管理发送的数据量，基于订阅者处理数据的能力。

2. **Subscriber（订阅者）**：
   - 订阅者从发布者接收数据。
   - 它可以请求发送特定数量的项目（使用 `subscription.request(n)` 方法）或取消订阅。

3. **Subscription（订阅）**：
   - 该接口连接发布者和订阅者。
   - 它允许订阅者通过请求一定数量的项目或取消订阅来控制数据流。

4. **Processor（处理器）**：
   - 处理器是一个同时充当发布者和订阅者的组件。
   - 这使得它能够从发布者接收数据，处理这些数据，然后将处理后的数据发布给一个或多个订阅者。

#### 背压的工作原理
1. 需求管理：订阅者可以根据其处理能力请求一定量的数据。例如，订阅者可能一次请求 10 个项目。
2. 流控制：如果订阅者处理数据的速度较慢，它可以请求更少的项目或停止请求。这向发布者发出信号，要求其减缓数据的发送速度。
3. 缓冲和丢弃：根据实现，发布者可能会缓冲数据，直到消费者能够处理为止。或者，如果数据超出某个限制，它可能会丢弃多余的数据（尽管这可能导致数据丢失）。
### 介绍 Java 9 Flow API
https://mrbird.cc/Java-9-Flow-API-Learn.html

https://juejin.cn/post/7104961299670368264

### reactor
https://github.com/reactor/reactor
和 Spring 深度继承，Spring 的 WebFlux 就在它的基础上实现。

Overview: Reactor is a fully non-blocking reactive programming foundation for the JVM. It provides a powerful and flexible model to build reactive applications.
Key Features:
Supports backpressure and asynchronous event processing.
Integrates seamlessly with Spring Framework, especially Spring WebFlux.
Offers two main types: Mono (0 or 1 element) and Flux (0 to N elements).
Built with a focus on performance and scalability.
Use Cases: Ideal for building reactive applications in conjunction with Spring, especially in microservices and cloud-native environments.

主要包含 reactor-core 和 reactor-netty
1. [reactor-core](https://github.com/reactor/reactor-core/):
Non-Blocking Reactive Streams Foundation for the JVM both implementing a Reactive Extensions inspired API and efficient event streaming support.
2. [reactor-netty](https://github.com/reactor/reactor-netty):
Reactor Netty offers non-blocking and backpressure-ready TCP/HTTP/UDP/QUIC clients & servers based on Netty framework.
3. [reactor-addons](https://github.com/reactor/reactor-addons/)
4. [reactor-pool](https://github.com/reactor/reactor-pool/)
### RxJava
RxJava (https://github.com/ReactiveX/RxJava)

Overview: RxJava is a library for composing asynchronous and event-based programs using observable sequences for the Java VM.
Key Features:
Extends the observer pattern to support sequences of data/events.
Provides a rich set of operators to compose and transform data flows.
Supports both backpressure (via Flowable) and non-backpressure (via Observable).
Allows for integration with Java 8+ features and is friendly with Android API.
Use Cases: Suitable for a wide range of applications, including Android development and Java-based server applications.

### Reactor vs RxJava
如果是后端的话，更建议学 Reactor。
[边学边练](https://github.com/reactor/lite-rx-api-hands-on)
Target Audience: Reactor is more focused on Spring developers and server-side applications, 
while RxJava is broader and caters to various Java applications, including Android.

Data Types: Reactor uses Mono and Flux, whereas RxJava offers Single, Maybe, Completable, Observable, and Flowable.

Concurrency Model: Both libraries support asynchronous processing, 
but Reactor has a stronger emphasis on integration with project reactor and Spring ecosystem.


### Spring WebFlux
The original web framework included in the Spring Framework, Spring Web MVC,
was purpose-built for the Servlet API and Servlet containers.
The reactive-stack web framework, Spring WebFlux, was added later in version 5.0. 
It is fully non-blocking, supports Reactive Streams back pressure, and runs on such servers as Netty, Undertow, and Servlet containers.

Both web frameworks mirror the names of their source modules (spring-webmvc and spring-webflux) 
and co-exist side by side in the Spring Framework. 
Each module is optional. Applications can use one or the other module or, 
in some cases, both — for example, Spring MVC controllers with the reactive WebClient.

https://docs.spring.io/spring-framework/reference/web/webflux.html
https://www.baeldung.com/spring-5-webclient
https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html
#### Spring MVC vs Spring WebFlux
![Spring MVC vs Spring WebFlux](docs/spring-mvc-vs-webflux.png)

If you have a large team, keep in mind the steep learning curve in the shift to non-blocking, functional, and declarative programming. A practical way to start without a full switch is to use the reactive WebClient. Beyond that, start small and measure the benefits. We expect that, for a wide range of applications, the shift is unnecessary. If you are unsure what benefits to look for, start by learning about how non-blocking I/O works (for example, concurrency on single-threaded Node.js) and its effects.
The key expected benefit of reactive and non-blocking is the ability to scale with a small, fixed number of threads and less memory

In Spring MVC (and servlet applications in general), it is assumed that applications can block the current thread, (for example, for remote calls). For this reason, servlet containers use a large thread pool to absorb potential blocking during request handling.

In Spring WebFlux (and non-blocking servers in general), it is assumed that applications do not block. Therefore, non-blocking servers use a small, fixed-size thread pool (event loop workers) to handle requests.

### todo
https://projectreactor.io/docs/core/release/reference/reactiveProgramming.html
https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html
