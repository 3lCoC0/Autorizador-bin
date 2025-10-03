package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.SubtypePlanRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.SubtypePlanLink;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypePlanEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypePlanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;

@Repository
@RequiredArgsConstructor
public class JpaSubtypePlanRepository implements SubtypePlanRepository {

    private final SubtypePlanJpaRepository repository;

    @Override
    public Mono<Integer> upsert(String subtypeCode, Long planId, String updatedBy) {
        return Mono.fromCallable(() -> {
                    SubtypePlanEntity entity = repository.findBySubtypeCode(subtypeCode)
                            .orElseGet(SubtypePlanEntity::new);
                    entity.setSubtypeCode(subtypeCode);
                    entity.setPlanId(planId);
                    OffsetDateTime now = OffsetDateTime.now();
                    if (entity.getCreatedAt() == null) {
                        entity.setCreatedAt(now);
                    }
                    entity.setUpdatedAt(now);
                    entity.setUpdatedBy(updatedBy);
                    repository.save(entity);
                    return 1;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<SubtypePlanLink> findBySubtype(String subtypeCode) {
        return Mono.defer(() -> Mono.justOrEmpty(repository.findBySubtypeCode(subtypeCode).map(SubtypePlanEntity::toDomain)))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
