package com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.use_case;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.inbound.UpdateBinUseCase;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.outbound.BinRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.bin.Bin;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppError;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppException;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public record UpdateBinService(BinRepository repo, BlockingTransactionExecutor txExecutor)
        implements UpdateBinUseCase {

    private static long ms(long t0) { return (System.nanoTime() - t0) / 1_000_000; }

    @Override
    public Mono<Bin> execute(String bin, String name, String typeBin, String typeAccount,
                             String compensationCod, String description,
                             String usesBinExt, Integer binExtDigits,
                             String updatedByNullable) {

        long t0 = System.nanoTime();
        log.debug("UC:UpdateBin:start bin={}, usesExt={}, extDigits={}", bin, usesBinExt, binExtDigits);

        return txExecutor.executeMono(() ->
                        repo.findById(bin)
                                .switchIfEmpty(Mono.error(new AppException(AppError.BIN_NOT_FOUND)))
                                .flatMap(current ->
                                        Mono.defer(() -> Mono.just(
                                                        current.updateBasics(name, typeBin, typeAccount,
                                                                compensationCod, description,
                                                                usesBinExt, binExtDigits, updatedByNullable)))
                                                .onErrorMap(IllegalArgumentException.class,
                                                        e -> new AppException(AppError.BIN_INVALID_DATA, e.getMessage()))
                                                .flatMap(repo::save)
                                )
                )
                .doOnSuccess(b -> log.info("UC:UpdateBin:done bin={}, elapsedMs={}", b.bin(), ms(t0)));
    }
}
