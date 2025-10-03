package com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.use_case;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.inbound.ChangeBinStatusUseCase;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.outbound.BinRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.bin.Bin;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppError;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppException;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public record ChangeBinStatusService(BinRepository repo, BlockingTransactionExecutor txExecutor)
        implements ChangeBinStatusUseCase {

    private static long ms(long t0) { return (System.nanoTime() - t0) / 1_000_000; }

    @Override
    public Mono<Bin> execute(String bin, String newStatus, String updatedByNullable) {
        long t0 = System.nanoTime();
        log.debug("UC:ChangeStatus:start bin={}, newStatus={}", bin, newStatus);


        if (!"A".equalsIgnoreCase(newStatus) && !"I".equalsIgnoreCase(newStatus)) {
            return Mono.error(new AppException(AppError.BIN_INVALID_DATA, "status debe ser 'A' o 'I'"));
        }

        return txExecutor.executeMono(() ->
                        repo.findById(bin)
                                .switchIfEmpty(Mono.error(new AppException(AppError.BIN_NOT_FOUND, "bin=" + bin)))
                                .flatMap(current ->
                                        Mono.defer(() -> Mono.just(current.changeStatus(newStatus, updatedByNullable)))
                                                .onErrorMap(IllegalArgumentException.class,
                                                        e -> new AppException(AppError.BIN_INVALID_DATA, e.getMessage()))
                                                .flatMap(repo::save)
                                )
                )
                .doOnSuccess(b -> log.info("UC:ChangeStatus:done bin={}, status={}, elapsedMs={}",
                        b.bin(), b.status(), ms(t0)));
    }
}
