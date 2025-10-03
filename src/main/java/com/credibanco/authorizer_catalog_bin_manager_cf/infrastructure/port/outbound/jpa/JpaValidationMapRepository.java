package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.outbound.ValidationMapRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationMap;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeValidationMapEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeValidationMapJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaValidationMapRepository implements ValidationMapRepository {

    private final SubtypeValidationMapJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsActive(String subtypeCode, String bin, Long validationId) {
        return blockingExecutor.mono(() -> repository.existsBySubtypeCodeAndBinAndValidationIdAndStatus(subtypeCode, bin, validationId, "A"));
    }

    @Override
    public Mono<ValidationMap> save(ValidationMap map) {
        return blockingExecutor.mono(() -> {
            SubtypeValidationMapEntity entity = repository.findBySubtypeCodeAndBinAndValidationId(map.subtypeCode(), map.bin(), map.validationId())
                    .orElseGet(SubtypeValidationMapEntity::new);
            entity.updateFromDomain(map);
            if (entity.getCreatedAt() == null) {
                entity.setCreatedAt(map.createdAt() != null ? map.createdAt() : OffsetDateTime.now());
            }
            if (entity.getUpdatedAt() == null) {
                entity.setUpdatedAt(entity.getCreatedAt());
            }
            repository.save(entity);
            return repository.findBySubtypeCodeAndBinAndValidationId(map.subtypeCode(), map.bin(), map.validationId())
                    .orElse(entity)
                    .toDomain();
        });
    }

    @Override
    public Mono<ValidationMap> findByNaturalKey(String subtypeCode, String bin, Long validationId) {
        return blockingExecutor.mono(() -> repository.findBySubtypeCodeAndBinAndValidationId(subtypeCode, bin, validationId)
                .map(SubtypeValidationMapEntity::toDomain)
                .orElse(null));
    }

    @Override
    public Flux<ValidationMap> findAll(String subtypeCode, String bin, String status, int page, int size) {
        return findByFilters(subtypeCode, bin, status, page, size);
    }

    @Override
    public Flux<ValidationMap> findResolved(String subtypeCode, String bin, String status, int page, int size) {
        return findByFilters(subtypeCode, bin, status, page, size);
    }

    private Flux<ValidationMap> findByFilters(String subtypeCode, String bin, String status, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        Specification<SubtypeValidationMapEntity> spec = Specification.where(null);
        if (subtypeCode != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("subtypeCode"), subtypeCode));
        }
        if (bin != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("bin"), bin));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        Specification<SubtypeValidationMapEntity> finalSpec = spec;
        Sort sort = Sort.by("subtypeCode").ascending()
                .and(Sort.by("bin").ascending())
                .and(Sort.by("validationId").ascending());
        return blockingExecutor.flux(() -> {
            List<ValidationMap> maps = repository.findAll(finalSpec,
                            PageRequest.of(p, s, sort))
                    .map(SubtypeValidationMapEntity::toDomain)
                    .getContent();
            return maps;
        });
    }
}
