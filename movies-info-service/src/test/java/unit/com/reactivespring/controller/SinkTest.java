package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {
    //Junit Test for
    @Test
    public void sink(){
        //given - precondition or setup
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        //when  - action or behaviour that we are going to test
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then - verify output
        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe(i -> {
            System.out.println("Subscriber 1:" + i);
        });

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe(i -> {
            System.out.println("Subscriber 2:" + i);
        });

        replaySink.tryEmitNext(3);

        Flux<Integer> integerFlux3 = replaySink.asFlux();
        integerFlux3.subscribe(i -> {
            System.out.println("Subscriber 3:" + i);
        });
    }

    //Junit Test for
    @Test
    public void sink_multicast(){
        //given - precondition or setup
        Sinks.Many<Integer> multiCastSink = Sinks.many().multicast().onBackpressureBuffer();

        //when  - action or behaviour that we are going to test
        multiCastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multiCastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then - verify output
        Flux<Integer> integerFlux1 = multiCastSink.asFlux();
        integerFlux1.subscribe(i -> {
            System.out.println("Subscriber1 :" + i);
        });
        Flux<Integer> integerFlux2 = multiCastSink.asFlux();
        integerFlux2.subscribe(i -> {
            System.out.println("Subcriber2 :" + i);
        });

        multiCastSink.tryEmitNext(3);
    }

    @Test
    public void sink_unicast(){
        //given - precondition or setup
        Sinks.Many<Integer> uniCastSink = Sinks.many().unicast().onBackpressureBuffer();

        //when  - action or behaviour that we are going to test
        uniCastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        uniCastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        //then - verify output
        Flux<Integer> integerFlux1 = uniCastSink.asFlux();
        integerFlux1.subscribe(i -> {
            System.out.println("Subscriber1 :" + i);
        });
        Flux<Integer> integerFlux2 = uniCastSink.asFlux();
        integerFlux2.subscribe(i -> {
            System.out.println("Subcriber2 :" + i);
        });

        uniCastSink.tryEmitNext(3);
    }
}
