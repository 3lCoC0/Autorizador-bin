package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.AgencyReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.AgencyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
@RequiredArgsConstructor
public class JpaAgencyReadOnlyRepository implements AgencyReadOnlyRepository {

    private final AgencyJpaRepository repository;

    @Override
    public Mono<Long> countActiveBySubtypeCode(String subtypeCode) {
        return Mono.fromCallable(() -> repository.countByIdSubtypeCodeAndStatus(subtypeCode, "A"))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
