package com.example.sleuthcorrelationdemo.resource;

import com.example.sleuthcorrelationdemo.service.HelloWorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.DefaultSpanNamer;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.TraceCallable;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


@RestController
@RequestMapping("/api")
public class HelloWorldResource {
    private static final Logger LOG = LoggerFactory.getLogger(HelloWorldResource.class);

    private static final String SPANNAME_HELLO_WORLD = "helloWorld";
    private static final String SPANNAME_HELLO_WORLD_TRACECALLABLE = "helloWorldTraceCallable";
    private static final String SPANNAME_HELLO_WORLD_TRACEABLE_EXEUCTOR_SERVICE= "helloWorldTraceableExecutorService";

    @Autowired
    private Tracer tracer;

    @Autowired
    @Qualifier("traceableExecutorService")
    private ExecutorService traceableExecutorService;

    @Autowired
    private HelloWorldService helloWorldService;

    @Autowired
    public HelloWorldResource(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    /**
     * Demonstrate the endpoint method without passing tracer to the Callable thread, so the helloWorldService.sayHello log will not have traceId and spanId
     */
    @SpanName(SPANNAME_HELLO_WORLD)
    @GetMapping(value = "/hello-world")
    Callable<ResponseEntity<String>> helloWorld(@RequestParam("name") String name) throws Exception {
        LOG.info("HelloWorldResource.helloWorld(name:{})", name);

        return new Callable<ResponseEntity<String>>() {
            @Override
            public ResponseEntity<String> call() throws Exception {
                return ResponseEntity.ok().body(helloWorldService.sayHello(name));
            }
        };
    }

    /**
     * Demonstrate the endpoint method passing tracer to the Callable thread wrapped by TraceCallable, so the helloWorldService.sayHello log will have traceId and spanId information
     */
    @SpanName(SPANNAME_HELLO_WORLD_TRACECALLABLE)
    @GetMapping(value = "/hello-world-trace-callable")
    Callable<ResponseEntity<String>> helloWorldTraceCallable(@RequestParam("name") String name) throws Exception {
        LOG.info("HelloWorldResource.helloWorldTraceCallable(name:{})", name);

        return new TraceCallable<>(this.tracer, new DefaultSpanNamer(), new Callable<ResponseEntity<String>>() {
            @Override
            public ResponseEntity<String> call() throws Exception {
                return ResponseEntity.ok().body(helloWorldService.sayHello(name));
            }

            @Override public String toString() {
                return SPANNAME_HELLO_WORLD_TRACECALLABLE;
            }
        });
    }


    @GetMapping(value = "/hello-world-async")
    ResponseEntity<String> helloWorldAsync(@RequestParam("name") String name) throws Exception {
        LOG.info("HelloWorldResource.helloWorldAsync(name:{})", name);

        helloWorldService.asyncSayHello(name);
        return ResponseEntity.ok().body("processed request asynchornously");
    }

    @SpanName(SPANNAME_HELLO_WORLD_TRACEABLE_EXEUCTOR_SERVICE)
    @GetMapping(value = "/hello-world-executor-service")
    ResponseEntity<String> helloWorldExecutorService(@RequestParam("name") String name) throws Exception {
        LOG.info("HelloWorldResource.helloWorldExecutorService(name:{})", name);

        Future<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            return helloWorldService.sayHello(name);
            }, traceableExecutorService);

        return ResponseEntity.ok().body(completableFuture.get());
    }

}
