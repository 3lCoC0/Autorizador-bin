package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.AgencyReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.AgencyJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class JpaAgencyReadOnlyRepository implements AgencyReadOnlyRepository {

    private final AgencyJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Long> countActiveBySubtypeCode(String subtypeCode) {
        return blockingExecutor.mono(() -> repository.countByIdSubtypeCodeAndStatus(subtypeCode, "A"));
    }
}
