package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.BinReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.BinJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class JpaBinReadOnlyRepository implements BinReadOnlyRepository {

    private final BinJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsById(String bin) {
        return blockingExecutor.mono(() -> repository.existsById(bin));
    }

    @Override
    public Mono<BinExtConfig> getExtConfig(String bin) {
        return blockingExecutor.mono(() -> repository.findById(bin)
                .map(entity -> new BinExtConfig(entity.getUsesBinExt(), entity.getBinExtDigits()))
                .orElse(null));
    }
}
