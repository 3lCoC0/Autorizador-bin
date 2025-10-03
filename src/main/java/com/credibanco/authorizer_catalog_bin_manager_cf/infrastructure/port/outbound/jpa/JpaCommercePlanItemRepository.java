package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.CommercePlanItemRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.PlanItem;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.CommercePlanItemEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.CommercePlanItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaCommercePlanItemRepository implements CommercePlanItemRepository {

    private final CommercePlanItemJpaRepository repository;

    @Override
    public Mono<PlanItem> insertMcc(Long planId, String mcc, String by) {
        return Mono.fromCallable(() -> repository.save(CommercePlanItemEntity.newMcc(planId, mcc, by, OffsetDateTime.now())).toDomain())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<PlanItem> changeStatus(Long planId, String value, String newStatus, String updatedBy) {
        return Mono.defer(() -> {
                    Optional<CommercePlanItemEntity> optional = findEntityByValue(planId, value);
                    if (optional.isEmpty()) {
                        return Mono.empty();
                    }
                    CommercePlanItemEntity entity = optional.get();
                    entity.setStatus(newStatus);
                    entity.setUpdatedAt(OffsetDateTime.now());
                    entity.setUpdatedBy(updatedBy);
                    return Mono.fromCallable(() -> repository.save(entity).toDomain());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<PlanItem> listItems(Long planId, String status, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        Specification<CommercePlanItemEntity> spec = Specification.where((root, query, cb) -> cb.equal(root.get("planId"), planId));
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        Specification<CommercePlanItemEntity> finalSpec = spec;
        return Flux.defer(() -> {
                    List<PlanItem> items = repository.findAll(finalSpec,
                                    PageRequest.of(p, s, Sort.by("planItemId").descending()))
                            .map(CommercePlanItemEntity::toDomain)
                            .getContent();
                    return Flux.fromIterable(items);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<PlanItem> insertMerchant(Long planId, String merchantId, String updatedBy) {
        return Mono.fromCallable(() -> repository.save(CommercePlanItemEntity.newMerchant(planId, merchantId, updatedBy, OffsetDateTime.now())).toDomain())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<PlanItem> findByValue(Long planId, String value) {
        return Mono.defer(() -> Mono.justOrEmpty(findEntityByValue(planId, value).map(CommercePlanItemEntity::toDomain)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<String> findExistingValues(Long planId, List<String> values) {
        if (values == null || values.isEmpty()) {
            return Flux.empty();
        }
        return Flux.defer(() -> Flux.fromIterable(repository.findExistingValues(planId, values)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Integer> insertMccBulk(Long planId, List<String> mccs, String by) {
        if (mccs == null || mccs.isEmpty()) {
            return Mono.just(0);
        }
        return Mono.fromCallable(() -> {
                    Set<String> existing = new HashSet<>(repository.findExistingValues(planId, mccs));
                    OffsetDateTime now = OffsetDateTime.now();
                    List<CommercePlanItemEntity> toSave = mccs.stream()
                            .filter(v -> !existing.contains(v))
                            .map(v -> CommercePlanItemEntity.newMcc(planId, v, by, now))
                            .toList();
                    repository.saveAll(toSave);
                    return toSave.size();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Integer> insertMerchantBulk(Long planId, List<String> mids, String by) {
        if (mids == null || mids.isEmpty()) {
            return Mono.just(0);
        }
        return Mono.fromCallable(() -> {
                    Set<String> existing = new HashSet<>(repository.findExistingValues(planId, mids));
                    OffsetDateTime now = OffsetDateTime.now();
                    List<CommercePlanItemEntity> toSave = mids.stream()
                            .filter(v -> !existing.contains(v))
                            .map(v -> CommercePlanItemEntity.newMerchant(planId, v, by, now))
                            .toList();
                    repository.saveAll(toSave);
                    return toSave.size();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsActiveByPlanId(Long planId) {
        return Mono.fromCallable(() -> repository.existsByPlanIdAndStatus(planId, "A"))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Optional<CommercePlanItemEntity> findEntityByValue(Long planId, String value) {
        return repository.findByPlanIdAndValue(planId, value, PageRequest.of(0, 1, Sort.by("planItemId").descending()))
                .stream()
                .findFirst();
    }
}
