package com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.use_case;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.inbound.CreatePlanUseCase;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.CommercePlanRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommercePlan;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommerceValidationMode;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppError;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.exception.AppException;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public record CreatePlanService(CommercePlanRepository repo, BlockingTransactionExecutor txExecutor)
        implements CreatePlanUseCase {
    @Override
    public Mono<CommercePlan> execute(String code, String name, CommerceValidationMode mode, String description, String by) {
        log.info("CreatePlanService IN code={} name={} mode={} by={}", code, name, mode, by);
        return txExecutor.executeMono(() ->
                        repo.existsByCode(code)
                                .flatMap(exists -> exists
                                        ? Mono.<CommercePlan>error(new AppException(AppError.PLAN_ALREADY_EXISTS))
                                        : Mono.fromCallable(() -> CommercePlan.createNew(code, name, mode, description, by))
                                        .onErrorMap(IllegalArgumentException.class,
                                                e -> new AppException(AppError.PLAN_INVALID_DATA, e.getMessage()))
                                        .flatMap(repo::save))
                )
                .doOnSuccess(p -> log.info("CreatePlanService OK code={} id={}", p.code(), p.planId()));
    }
}
