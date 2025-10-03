package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.IdTypeReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.IdTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
@RequiredArgsConstructor
public class JpaIdTypeReadOnlyRepository implements IdTypeReadOnlyRepository {

    private final IdTypeJpaRepository repository;

    @Override
    public Mono<Boolean> existsById(String idType) {
        return Mono.fromCallable(() -> repository.existsById(idType))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
