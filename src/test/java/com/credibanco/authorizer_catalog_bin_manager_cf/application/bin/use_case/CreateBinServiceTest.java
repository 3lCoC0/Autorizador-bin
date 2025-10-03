package com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.use_case;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.outbound.BinRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.bin.Bin;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppException;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.support.ResourcelessTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CreateBinServiceTest {

    private BinRepository repository;
    private CreateBinService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BinRepository.class);
        BlockingExecutor blockingExecutor = new BlockingExecutor();
        TransactionTemplate template = new TransactionTemplate(new ResourcelessTransactionManager());
        BlockingTransactionExecutor txExecutor = new BlockingTransactionExecutor(template, blockingExecutor);
        service = new CreateBinService(repository, txExecutor);
    }

    @Test
    void createsBinWhenDoesNotExist() {
        Mockito.when(repository.existsById("123456")).thenReturn(Mono.just(false));
        Bin expected = Bin.createNew("123456", "BIN", "DEBITO", "01", null, "desc", "N", null, "me");
        Mockito.when(repository.save(Mockito.any())).thenReturn(Mono.just(expected));

        StepVerifier.create(service.execute("123456", "BIN", "DEBITO", "01", null, "desc", "N", null, "me"))
                .expectNext(expected)
                .verifyComplete();

        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    void failsWhenBinAlreadyExists() {
        Mockito.when(repository.existsById("123456")).thenReturn(Mono.just(true));

        StepVerifier.create(service.execute("123456", "BIN", "DEBITO", "01", null, "desc", "N", null, "me"))
                .expectError(AppException.class)
                .verify();
    }
}
