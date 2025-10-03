package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.rule.port.outbound.ValidationRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.Validation;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeValidationEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeValidationJpaRepository;
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
public class JpaValidationRepository implements ValidationRepository {

    private final SubtypeValidationJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsByCode(String code) {
        return blockingExecutor.mono(() -> repository.existsByCode(code));
    }

    @Override
    public Mono<Validation> save(Validation v) {
        return blockingExecutor.mono(() -> {
            SubtypeValidationEntity entity = repository.findByCode(v.code())
                    .orElseGet(SubtypeValidationEntity::new);
            entity.updateFromDomain(v);
            SubtypeValidationEntity saved = repository.save(entity);
            return repository.findById(saved.getValidationId()).orElse(saved).toDomain();
        });
    }

    @Override
    public Mono<Validation> findByCode(String code) {
        return blockingExecutor.mono(() -> repository.findByCode(code)
                .map(SubtypeValidationEntity::toDomain)
                .orElse(null));
    }

    @Override
    public Mono<Validation> findById(Long id) {
        return blockingExecutor.mono(() -> repository.findById(id)
                .map(SubtypeValidationEntity::toDomain)
                .orElse(null));
    }

    @Override
    public Flux<Validation> findAll(String status, String search, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        Specification<SubtypeValidationEntity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (search != null) {
            String like = "%" + search.toUpperCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.upper(root.get("code")), like),
                    cb.like(cb.upper(root.get("description")), like)
            ));
        }
        Specification<SubtypeValidationEntity> finalSpec = spec;
        return blockingExecutor.flux(() -> {
            List<Validation> validations = repository.findAll(finalSpec,
                            PageRequest.of(p, s, Sort.by("code").ascending()))
                    .map(SubtypeValidationEntity::toDomain)
                    .getContent();
            return validations;
        });
    }
}
