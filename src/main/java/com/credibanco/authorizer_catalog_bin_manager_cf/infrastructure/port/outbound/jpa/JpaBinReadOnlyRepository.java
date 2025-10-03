package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.BinReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.BinJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository
@RequiredArgsConstructor
public class JpaBinReadOnlyRepository implements BinReadOnlyRepository {

    private final BinJpaRepository repository;

    @Override
    public Mono<Boolean> existsById(String bin) {
        return Mono.fromCallable(() -> repository.existsById(bin))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<BinExtConfig> getExtConfig(String bin) {
        return Mono.defer(() -> Mono.justOrEmpty(repository.findById(bin)
                        .map(entity -> new BinExtConfig(entity.getUsesBinExt(), entity.getBinExtDigits()))))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
