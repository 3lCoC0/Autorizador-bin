package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.SubtypeRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.subtype.Subtype;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeEntityId;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaSubtypeRepository implements SubtypeRepository {

    private final SubtypeJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsByPk(String bin, String subtypeCode) {
        return blockingExecutor.mono(() -> repository.existsById(new SubtypeEntityId(bin, subtypeCode)));
    }

    @Override
    public Mono<Boolean> existsByBinAndExt(String bin, String binExt) {
        if (binExt == null) {
            return Mono.just(false);
        }
        return blockingExecutor.mono(() -> repository.existsByBinAndBinExt(bin, binExt));
    }

    @Override
    public Mono<Subtype> save(Subtype entity) {
        return blockingExecutor.mono(() -> {
            SubtypeEntityId id = new SubtypeEntityId(entity.bin(), entity.subtypeCode());
            repository.save(SubtypeEntity.fromDomain(entity));
            return repository.findById(id).map(SubtypeEntity::toDomain).orElseThrow();
        });
    }

    @Override
    public Mono<Subtype> findByPk(String bin, String subtypeCode) {
        return blockingExecutor.mono(() -> repository.findById(new SubtypeEntityId(bin, subtypeCode))
                .map(SubtypeEntity::toDomain)
                .orElse(null));
    }

    @Override
    public Flux<Subtype> findAll(String binFilter, String codeFilter, String statusFilter, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        Specification<SubtypeEntity> spec = Specification.where(null);
        if (binFilter != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("id").get("bin"), binFilter));
        }
        if (codeFilter != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("id").get("subtypeCode"), codeFilter));
        }
        if (statusFilter != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusFilter));
        }
        Specification<SubtypeEntity> finalSpec = spec;
        return blockingExecutor.flux(() -> {
            List<Subtype> subtypes = repository.findAll(finalSpec,
                            PageRequest.of(p, s, Sort.by("id.bin").ascending().and(Sort.by("id.subtypeCode").ascending())))
                    .map(SubtypeEntity::toDomain)
                    .getContent();
            return subtypes;
        });
    }
}
