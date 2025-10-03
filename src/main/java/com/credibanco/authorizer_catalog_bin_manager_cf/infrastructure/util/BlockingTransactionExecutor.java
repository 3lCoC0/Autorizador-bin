package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class BlockingTransactionExecutor {

    private final TransactionTemplate transactionTemplate;
    private final BlockingExecutor blockingExecutor;

    public <T> Mono<T> executeMono(Supplier<Mono<T>> action) {
        Objects.requireNonNull(action, "action");
        return blockingExecutor.mono(() ->
                transactionTemplate.execute(status -> action.get().block()));
    }

    public <T> Flux<T> executeFlux(Supplier<Flux<T>> action) {
        Objects.requireNonNull(action, "action");
        return blockingExecutor.<List<T>>mono(() ->
                        transactionTemplate.execute(status -> action.get().collectList().block()))
                .flatMapMany(list -> list == null ? Flux.empty() : Flux.fromIterable(list));
    }
}
