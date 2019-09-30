/*
 * Copyright (c) 2018 Ryanair Ltd. All rights reserved.
 */
package com.example.demo.adapters.rest;

import com.example.demo.adapters.db.HutRepository;
import com.example.demo.domain.Hut;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;


public class HutApi {
    private HutRepository repository;

    public HutApi(HutRepository repository) {
        this.repository = repository;
    }

    private Function<Hut, Mono<ServerResponse>> ok = hut -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(hut), Hut.class);
    private Function<Throwable, Mono<ServerResponse>> notFound = __ -> ServerResponse.notFound().build();
    private Function<Throwable, Mono<ServerResponse>> fromException = error ->
            ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(error.getMessage()), String.class);

   public RouterFunction<ServerResponse> apiRoot =
            RouterFunctions.nest(
                RequestPredicates.path("/huts"),
                RouterFunctions.route(RequestPredicates.GET("/{id}"), this::getById)
                    .andRoute(RequestPredicates.GET("/").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), this::get)
                    .andRoute(RequestPredicates.POST("/").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), this::post)
                    .andRoute(RequestPredicates.PUT("/{id}").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)), this::putById)
            );

    private Mono<ServerResponse> get(ServerRequest request) {
        return repository.get()
                .flatMap(data -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(data), Hut[].class))
                .onErrorResume(Throwable.class, fromException)
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<ServerResponse> post(ServerRequest request) {
        return request.bodyToMono(Hut.class)
                .flatMap(repository::add)
                .flatMap(ok)
                .onErrorResume(Throwable.class, fromException)
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<ServerResponse> putById(ServerRequest request) {
        Function<Hut, Mono<Hut>> ensureId = r ->
                Mono.just(request.pathVariable("id")).map(id -> new Hut(id, r.getName()));

        return request.bodyToMono(Hut.class)
                .flatMap(ensureId)
                .flatMap(repository::update)
                .flatMap(ok)
                .onErrorResume(Throwable.class, fromException)
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<ServerResponse> getById(ServerRequest request) {
        return Mono.fromSupplier(() -> request.pathVariable("id"))
                .flatMap(repository::get)
                .flatMap(ok)
                .onErrorResume(RuntimeException.class, notFound)
                .subscribeOn(Schedulers.elastic());
    }
}
