package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.outbound.BinRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.bin.Bin;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.BinEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.BinJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaBinRepository implements BinRepository {

    private final BinJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsById(String bin) {
        return blockingExecutor.mono(() -> repository.existsById(bin));
    }

    @Override
    public Mono<Bin> save(Bin bin) {
        return blockingExecutor.mono(() -> {
            repository.save(BinEntity.fromDomain(bin));
            return repository.findById(bin.bin()).map(BinEntity::toDomain).orElseThrow();
        });
    }

    @Override
    public Mono<Bin> findById(String bin) {
        return blockingExecutor.mono(() ->
                repository.findById(bin).map(BinEntity::toDomain).orElse(null));
    }

    @Override
    public Flux<Bin> findAll(int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        return blockingExecutor.flux(() -> {
            List<Bin> bins = repository.findAll(PageRequest.of(p, s, Sort.by("bin").ascending()))
                    .map(BinEntity::toDomain)
                    .getContent();
            return bins;
        });
    }
}
