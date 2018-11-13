# sleuth-correlation-demo
A simple demo how to use ```spring-cloud-sleuth```

### How to run the application:
```
mvn spring-boot:run
```

### Case 1 (negative case) - Callable which is not tracable
```$xslt
http://localhost:8081/api/hello-world?name=def
```

```$xslt
14-11-2018 00:57:14.696  [app:hello-world-ms,traceId:a2ac762309f028d9,spanId:a2ac762309f028d9,parentSpanId:] 16202 [http-nio-8081-exec-5] INFO  com.example.sleuthcorrelationdemo.resource.HelloWorldResource.helloWorld - HelloWorldResource.helloWorld(name:def)
14-11-2018 00:57:14.697  [app:hello-world-ms,traceId:,spanId:,parentSpanId:] 16202 [MvcAsync2] INFO  com.example.sleuthcorrelationdemo.service.HelloWorldService.sayHello - HelloWorldService.sayHello(name:def)
```

### Case 2 - Use of ```TracableCallable```
```$xslt
http://localhost:8081/api/hello-world-trace-callable?name=abc
```

```$xslt
14-11-2018 00:52:43.832  [app:hello-world-ms,traceId:2dd9294e8ee3bd90,spanId:2dd9294e8ee3bd90,parentSpanId:] 16202 [http-nio-8081-exec-1] INFO  com.example.sleuthcorrelationdemo.resource.HelloWorldResource.helloWorldTraceCallable - HelloWorldResource.helloWorldTraceCallable(name:abc)
14-11-2018 00:52:43.872  [app:hello-world-ms,traceId:2dd9294e8ee3bd90,spanId:2454fe6759ff42e0,parentSpanId:2dd9294e8ee3bd90] 16202 [MvcAsync1] INFO  com.example.sleuthcorrelationdemo.service.HelloWorldService.sayHello - HelloWorldService.sayHello(name:abc)
```

### Case 3 - Call @Async service method with ```LazyTraceExecutor```
```$xslt
http://localhost:8081/api/hello-world-async?name=abc
```

```$xslt
14-11-2018 01:00:46.734  [app:hello-world-ms,traceId:f3d608772307d329,spanId:f3d608772307d329,parentSpanId:] 16202 [http-nio-8081-exec-8] INFO  com.example.sleuthcorrelationdemo.resource.HelloWorldResource.helloWorldAsync - HelloWorldResource.helloWorldAsync(name:abc)
14-11-2018 01:00:46.757  [app:hello-world-ms,traceId:f3d608772307d329,spanId:8aa9560401d9f182,parentSpanId:f3d608772307d329] 16202 [AsyncThreadPoolTaskExecutor-1] INFO  com.example.sleuthcorrelationdemo.service.HelloWorldService.asyncSayHello - HelloWorldService.asyncSayHello(name:abc)
```

### Case 4 - Use of ```TraceableExecutorService```
```$xslt
http://localhost:8081/api/hello-world-executor-service?name=abc
```

```$xslt
14-11-2018 01:07:30.821  [app:hello-world-ms,traceId:0160ce210fd38d9b,spanId:0160ce210fd38d9b,parentSpanId:] 16202 [http-nio-8081-exec-10] INFO  com.example.sleuthcorrelationdemo.resource.HelloWorldResource.helloWorldExecutorService - HelloWorldResource.helloWorldExecutorService(name:abc)
14-11-2018 01:07:30.825  [app:hello-world-ms,traceId:0160ce210fd38d9b,spanId:0160ce210fd38d9b,parentSpanId:0160ce210fd38d9b] 16202 [pool-1-thread-1] INFO  com.example.sleuthcorrelationdemo.service.HelloWorldService.sayHello - HelloWorldService.sayHello(name:abc)
```
