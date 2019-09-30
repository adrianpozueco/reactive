/*
 * Copyright (c) 2018 Ryanair Ltd. All rights reserved.
 */
package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class Play {
 static class Service{
     public String get() {
         throw new RuntimeException("errorro");
//         return "ola";
     }
 }

    @Test
    public void tt() {
        Service service = new Service();
        Mono<String> d = Mono.fromSupplier(service::get);
        Mono<String> a = Mono.just("Hello");
        Mono<String> b = Mono.just(" World");


        Mono.zip(a, b, (s, s2) -> s + s2).doOnSuccess(System.out::println).subscribe();

        d.flatMap(s -> Mono.just(s + " q ase"))
        .doOnError(throwable -> System.out.println(throwable.getMessage()))
        .doOnSuccess(System.out::println)
        .subscribe();
    }

 }
