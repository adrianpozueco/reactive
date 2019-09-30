/*
 * Copyright (c) 2018 Ryanair Ltd. All rights reserved.
 */
package com.example.demo.adapters.db;

import com.example.demo.domain.Hut;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HutRepository {

    private ConcurrentHashMap<String, Hut> huts;

    public HutRepository(ConcurrentHashMap<String, Hut> huts) {
        this.huts = huts;
    }

    private Mono<String> makeId() {
        return Mono.fromSupplier(() -> UUID.randomUUID().toString());
    }

    private Hut store(Hut toStore) {
        huts.put(toStore.getId(), toStore);
        return toStore;
    }

    public Mono<Hut[]> get() {
        return Mono.fromSupplier(() -> huts.values().toArray(new Hut[huts.size()]));
    }

    public Mono<Hut> get(String id) {
        return Mono.fromSupplier(() -> huts.get(id));
    }

    public Mono<Hut> add(Hut rawHut) {
        return makeId().map(id -> store(rawHut.toBuilder().id(id).build()));
    }

    public Mono<Hut> update(Hut hut) {
        return Mono.fromSupplier(() -> store(hut));
    }

}
