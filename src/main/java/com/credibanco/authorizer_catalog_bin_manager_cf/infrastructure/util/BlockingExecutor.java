package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;
import java.util.concurrent.Callable;

@Component
public class BlockingExecutor {

    public <T> Mono<T> mono(Callable<T> task) {
        return Mono.fromCallable(() -> Optional.ofNullable(task.call()))
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public <T> Flux<T> flux(Callable<Iterable<T>> task) {
        return Mono.fromCallable(() -> Optional.ofNullable(task.call()))
                .flatMapMany(optional -> optional.map(Flux::fromIterable).orElseGet(Flux::empty))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
