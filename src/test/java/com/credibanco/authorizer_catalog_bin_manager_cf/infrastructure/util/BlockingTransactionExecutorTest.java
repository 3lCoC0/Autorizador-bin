package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.ResourcelessTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BlockingTransactionExecutorTest {

    private BlockingTransactionExecutor executor;

    @BeforeEach
    void setUp() {
        BlockingExecutor blockingExecutor = new BlockingExecutor();
        TransactionTemplate template = new TransactionTemplate(new ResourcelessTransactionManager());
        executor = new BlockingTransactionExecutor(template, blockingExecutor);
    }

    @Test
    void executeMonoRunsWithinTransaction() {
        StepVerifier.create(executor.executeMono(() -> Mono.fromCallable(() -> "ok")))
                .expectNext("ok")
                .verifyComplete();
    }

    @Test
    void executeMonoPropagatesErrors() {
        StepVerifier.create(executor.executeMono(() -> Mono.error(new IllegalStateException("boom"))))
                .expectErrorMatches(e -> e instanceof IllegalStateException && "boom".equals(e.getMessage()))
                .verify();
    }
}
