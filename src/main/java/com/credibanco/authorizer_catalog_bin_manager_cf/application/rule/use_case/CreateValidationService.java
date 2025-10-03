package com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.use_case;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.inbound.CreateValidationUseCase;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.outbound.ValidationRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.Validation;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationDataType;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppError;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppException;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public record CreateValidationService(ValidationRepository repo, BlockingTransactionExecutor txExecutor)
        implements CreateValidationUseCase {

    private static long ms(long t0) { return (System.nanoTime()-t0)/1_000_000; }

    @Override
    public Mono<Validation> execute(String code, String description, ValidationDataType type, String createdByNullable) {
        long t0 = System.nanoTime();
        return txExecutor.executeMono(() ->
                        repo.existsByCode(code)
                                .flatMap(exists -> exists
                                        ? Mono.error(new AppException(AppError.RULES_VALIDATION_ALREADY_EXISTS))
                                        : Mono.fromCallable(() -> Validation.createNew(code, description, type, createdByNullable))
                                        .onErrorMap(IllegalArgumentException.class,
                                                e -> new AppException(AppError.RULES_VALIDATION_INVALID_DATA, e.getMessage()))
                                        .flatMap(repo::save)
                                )
                )
                .doOnSuccess(v -> log.info("UC:Validation:Create:done code={} elapsedMs={}", code, ms(t0)));
    }
}
