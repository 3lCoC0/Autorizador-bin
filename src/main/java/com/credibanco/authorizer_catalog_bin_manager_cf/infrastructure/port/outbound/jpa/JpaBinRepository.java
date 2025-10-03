package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.outbound.BinRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.bin.Bin;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.BinEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.BinJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaBinRepository implements BinRepository {

    private final BinJpaRepository repository;

    @Override
    public Mono<Boolean> existsById(String bin) {
        return Mono.fromCallable(() -> repository.existsById(bin))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Bin> save(Bin bin) {
        return Mono.fromCallable(() -> {
                    repository.save(BinEntity.fromDomain(bin));
                    return repository.findById(bin.bin()).map(BinEntity::toDomain).orElseThrow();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Bin> findById(String bin) {
        return Mono.defer(() -> Mono.justOrEmpty(repository.findById(bin).map(BinEntity::toDomain)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Bin> findAll(int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        return Flux.defer(() -> {
                    List<Bin> bins = repository.findAll(PageRequest.of(p, s, Sort.by("bin").ascending()))
                            .map(BinEntity::toDomain)
                            .getContent();
                    return Flux.fromIterable(bins);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
