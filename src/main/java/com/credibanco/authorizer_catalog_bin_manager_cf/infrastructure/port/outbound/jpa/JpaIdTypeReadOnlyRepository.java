package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.IdTypeReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.IdTypeJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class JpaIdTypeReadOnlyRepository implements IdTypeReadOnlyRepository {

    private final IdTypeJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsById(String idType) {
        return blockingExecutor.mono(() -> repository.existsById(idType));
    }
}
