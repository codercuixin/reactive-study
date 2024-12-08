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
订阅者向发布者发出信号，要求其减慢数据的发送速度（通过 subscription.request(int n) 实现）, 发布者根据实现可能选择不同的策略。
比如，发布者可能会缓冲数据，直到消费者能够处理为止；或者，如果数据超出某个限制，它可能会丢弃多余的数据（尽管这可能导致数据丢失）。

#### 热序列（Hot sequence）和冷序列（Cold sequence）
热序列（Hot sequence）和冷序列（Cold sequence）。
这两个概念主要描述了响应式流（reactive stream）如何对订阅者（subscribers）作出反应。

1. 冷序列（Cold sequence）：

冷序列为每个订阅者从头开始。这意味着，如果数据源是一个HTTP请求，那么每个订阅都会触发一个新的HTTP请求。
冷序列的特点在于，它在订阅时才启动，并且每个订阅者都会经历相同的数据流从头到尾的完整过程。
2. 热序列（Hot sequence）：

热序列不会为每个订阅者从头开始。迟到的订阅者只会接收到他们订阅之后发出的信号。
一些热响应式流可以缓存或部分或全部重放发射的历史。这意味着即使订阅者晚些时候加入，他们也能接收到之前发生的数据。
从一般意义上讲，热序列甚至可以在没有订阅者监听的情况下发射数据，这违反了“在你订阅之前什么都不会发生”的规则。

### 介绍 Java 9 Flow API
https://mrbird.cc/Java-9-Flow-API-Learn.html

https://juejin.cn/post/7104961299670368264

### [reactor](https://github.com/reactor/reactor)
[边学边练](https://github.com/reactor/lite-rx-api-hands-on)

[官方文档](https://projectreactor.io/docs/core/release/reference/aboutDoc.html)

和 Spring 深度继承，Spring 的 WebFlux 就在它的基础上实现。
1. Flux: 0..N 个项的反应序列；可组合；丰富的 Operator;实现了 Reactive Stream 的 Publisher 接口
![flux](images/flux.png)

2. Mono：表示单值或空（0..1）；可组合；丰富的 Operator;实现了 Reactive Stream 的 Publisher 接口
![mono](images/mono.png)
### [RxJava](https://github.com/ReactiveX/RxJava)
#### 概述
RxJava 是一个用于在 Java 虚拟机上通过可观察序列组合异步和基于事件的程序的库。

#### 关键特性
- 扩展了观察者模式，以支持数据/事件序列。
- 提供丰富的操作符，用于组合和转化数据流。
- 支持背压（通过 Flowable）和非背压（通过 Observable）。
- 允许与 Java 8 及以上的特性集成，并与 Android API 兼容。

#### 使用案例
适用于广泛的应用程序，包括 Android 开发和基于 Java 的服务器应用程序。

### Reactor vs RxJava
如果是后端的话，更建议学 Reactor。
#### 目标受众
Reactor 更加关注于 Spring 开发人员和服务器端应用程序，而 RxJava 的受众更广泛，适用于各种 Java 应用程序，包括 Android。

#### 数据类型
Reactor 使用 Mono 和 Flux，而 RxJava 提供 Single、Maybe、Completable、Observable 和 Flowable。

#### 并发模型
这两个库都支持异步处理，但 Reactor 更加强调与 Project Reactor 和 Spring 生态系统的集成。


### Reactor vs CompletableFuture
1. Reactor：
模型：基于反应式编程，支持异步数据流和背压。
数据类型：使用 Flux（多项）和 Mono（单项）。
错误处理：提供强大的错误处理机制，如 onErrorResume。
功能强大：可组合性和可读性；数据作为流，通过丰富的运算符进行操作；在你订阅之前什么都不会发生；背压（消费者向生产者发出信号，告知其发送频率过高）； 高级但高价值的抽象，与并发无关
适用场景：适合高并发和事件驱动的应用，如微服务。
2. CompletableFuture：
模型：基于传统异步编程，处理单个异步任务。
数据类型：表示未来的单一结果。
错误处理：提供基本的错误处理，如 exceptionally。
适用场景：适合简单的异步任务，易于使用。

https://projectreactor.io/docs/core/release/reference/reactiveProgramming.html#asynchronicity-to-the-rescue

### Spring WebFlux
原始的网络框架 Spring Web MVC 是为 Servlet API 和 Servlet 容器专门构建的。
反应式栈网络框架 Spring WebFlux 在 5.0 版本中后添加。它是完全非阻塞的，支持反应式流的背压，并可以在 Netty、Undertow 和 Servlet 容器等服务器上运行。


这两个网络框架的名称与其源模块（spring-webmvc 和 spring-webflux）相对应，并在 Spring 框架中并存。
每个模块都是可选的。应用程序可以使用其中一个模块，或者在某些情况下同时使用两个模块，例如，将 Spring MVC 控制器与反应式 WebClient 一起使用。

https://docs.spring.io/spring-framework/reference/web/webflux.html
https://www.baeldung.com/spring-5-webclient
https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html
#### Spring MVC vs Spring WebFlux
![Spring MVC vs Spring WebFlux](images/spring-mvc-vs-webflux.png)

如果您有一个大型团队，请注意转向非阻塞、函数式和声明式编程的陡峭学习曲线。
一个实用的开始方式是使用反应式 WebClient，而不是完全切换。除此之外，可以从小处着手并衡量收益。我们认为，对于广泛的应用程序来说，这种转变是没有必要的。

如果您不确定要寻找哪些好处，可以先了解非阻塞 I/O 是如何工作的（例如，单线程 Node.js 上的并发）及其影响。
**反应式和非阻塞的关键预期好处是能够以较小的固定线程数和更少的内存进行扩展。**

在 Spring MVC（以及一般的 Servlet 应用程序）中，假设应用程序可以阻塞当前线程（例如，用于远程调用）。因此，Servlet 容器使用大型线程池来吸收请求处理期间的潜在阻塞。

在 Spring WebFlux（以及一般的非阻塞服务器）中，假设应用程序不阻塞。因此，非阻塞服务器使用小型固定大小的线程池（事件循环工作线程）来处理请求。
### 常见问题
#### 1.如果之前的项目里面用过 webClient.xx.block() 可能需要更改。
java.lang.IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread reactor-http-nio-2
at reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:87) ~[reactor-core-3.6.9.jar:3.6.9]
at reactor.core.publisher.Mono.block(Mono.java:1779) ~[reactor-core-3.6.9.jar:3.6.9]

#### 2. 最好从一个项目开始搞起，不要一开始就动重要的大项目。
