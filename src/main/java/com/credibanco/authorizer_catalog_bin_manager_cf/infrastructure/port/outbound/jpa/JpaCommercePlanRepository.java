package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.CommercePlanRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommercePlan;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.CommercePlanEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.CommercePlanJpaRepository;
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
public class JpaCommercePlanRepository implements CommercePlanRepository {

    private final CommercePlanJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsByCode(String planCode) {
        return blockingExecutor.mono(() -> repository.existsByPlanCode(planCode));
    }

    @Override
    public Mono<CommercePlan> findByCode(String planCode) {
        return blockingExecutor.mono(() -> repository.findByPlanCode(planCode)
                .map(CommercePlanEntity::toDomain)
                .orElse(null));
    }

    @Override
    public Flux<CommercePlan> findAll(String status, String q, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        Specification<CommercePlanEntity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (q != null) {
            String like = "%" + q.toUpperCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.upper(root.get("planCode")), like),
                    cb.like(cb.upper(root.get("planName")), like)
            ));
        }
        Specification<CommercePlanEntity> finalSpec = spec;
        return blockingExecutor.flux(() -> {
            List<CommercePlan> plans = repository.findAll(finalSpec,
                            PageRequest.of(p, s, Sort.by("planCode").ascending()))
                    .map(CommercePlanEntity::toDomain)
                    .getContent();
            return plans;
        });
    }

    @Override
    public Mono<CommercePlan> save(CommercePlan plan) {
        return blockingExecutor.mono(() -> {
            CommercePlanEntity entity = repository.findByPlanCode(plan.code())
                    .orElseGet(CommercePlanEntity::new);
            entity.updateFromDomain(plan);
            CommercePlanEntity saved = repository.save(entity);
            return repository.findById(saved.getPlanId()).orElse(saved).toDomain();
        });
    }
}
