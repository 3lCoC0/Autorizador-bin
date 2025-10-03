package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class JpaSubtypeReadOnlyRepository implements
        com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.outbound.SubtypeReadOnlyRepository,
        com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.SubtypeReadOnlyRepository,
        com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.outbound.SubtypeReadOnlyRepository {

    private final SubtypeJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> isActive(String subtypeCode) {
        return blockingExecutor.mono(() -> repository.existsActiveBySubtypeCode(subtypeCode));
    }

    @Override
    public Mono<Boolean> existsByCode(String subtypeCode) {
        return blockingExecutor.mono(() -> repository.existsBySubtypeCode(subtypeCode));
    }

    @Override
    public Mono<Boolean> existsByCodeAndBinEfectivo(String code, String binEfectivo) {
        return blockingExecutor.mono(() -> repository.existsBySubtypeCodeAndBin(code, binEfectivo));
    }
}
