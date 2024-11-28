### reactive stream
reactive stream：asynchronous stream processing with non-blocking backpressure.

[官网](https://www.reactive-streams.org/)

[规范](https://github.com/reactive-streams/reactive-streams-jvm)
#### Key Concepts
Reactive Streams:
The reactive streams specification defines how to handle asynchronous stream processing with non-blocking backpressure. 
It consists of four key interfaces: Publisher, Subscriber, Subscription, and Processor.
1. Publisher: 
A Publisher sends data to subscribers. 
It has the responsibility to manage how much data it sends based on the subscriber's capacity to handle it.
2. Subscriber:
A Subscriber receives data from the Publisher. 
It can request a specific number of items to be sent (using the request(n) method) or cancel the subscription.
3. Subscription:
This interface connects a Publisher and a Subscriber. It allows the subscriber to control the flow of data by requesting a certain number of items or canceling the subscription.
4. Processor:
   Processor is a component that acts as both a Publisher and a Subscriber. This allows it to receive data from a Publisher, process that data, and then publish the processed data to one or more Subscribers.

#### How Backpressure Works
1. Demand Management: The subscriber can request a certain amount of data from the producer based 
on its processing capacity. For example, a subscriber might request 10 items at a time.
2. Flow Control: If a subscriber is slow to process data, it can request fewer items or stop requesting altogether. 
This signals the publisher to slow down data emission.
3. Buffering and Dropping: Depending on the implementation, a publisher might buffer data until the consumer can handle it. 
Alternatively, it might drop excess data if it exceeds a certain limit (though this can lead to data loss).

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
