package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
@RequiredArgsConstructor
public class JpaSubtypeReadOnlyRepository implements
        com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.outbound.SubtypeReadOnlyRepository,
        com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.SubtypeReadOnlyRepository,
        com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.outbound.SubtypeReadOnlyRepository {

    private final SubtypeJpaRepository repository;

    @Override
    public Mono<Boolean> isActive(String subtypeCode) {
        return Mono.fromCallable(() -> repository.existsActiveBySubtypeCode(subtypeCode))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByCode(String subtypeCode) {
        return Mono.fromCallable(() -> repository.existsBySubtypeCode(subtypeCode))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByCodeAndBinEfectivo(String code, String binEfectivo) {
        return Mono.fromCallable(() -> repository.existsBySubtypeCodeAndBin(code, binEfectivo))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
