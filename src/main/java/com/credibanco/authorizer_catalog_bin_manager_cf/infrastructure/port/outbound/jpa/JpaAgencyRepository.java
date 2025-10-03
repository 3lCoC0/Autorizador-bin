package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.outbound.AgencyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.agency.Agency;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntityId;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.AgencyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaAgencyRepository implements AgencyRepository {

    private final AgencyJpaRepository repository;

    @Override
    public Mono<Boolean> existsByPk(String subtypeCode, String agencyCode) {
        return Mono.fromCallable(() -> repository.existsById(new AgencyEntityId(subtypeCode, agencyCode)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Agency> save(Agency aggregate) {
        return Mono.fromCallable(() -> {
                    AgencyEntityId id = new AgencyEntityId(aggregate.subtypeCode(), aggregate.agencyCode());
                    repository.save(AgencyEntity.fromDomain(aggregate));
                    return repository.findById(id).map(AgencyEntity::toDomain).orElseThrow();
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Agency> findByPk(String subtypeCode, String agencyCode) {
        return Mono.defer(() -> Mono.justOrEmpty(repository.findById(new AgencyEntityId(subtypeCode, agencyCode)).map(AgencyEntity::toDomain)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Agency> findAll(String subtypeCode, String status, String search, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        Specification<AgencyEntity> spec = Specification.where(null);
        if (subtypeCode != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("id").get("subtypeCode"), subtypeCode));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (search != null) {
            String like = "%" + search.toUpperCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.upper(root.get("name")), like),
                    cb.like(cb.upper(root.get("id").get("agencyCode")), like)
            ));
        }
        Specification<AgencyEntity> finalSpec = spec;
        return Flux.defer(() -> {
                    List<Agency> agencies = repository.findAll(finalSpec,
                                    PageRequest.of(p, s, Sort.by("id.subtypeCode").ascending().and(Sort.by("id.agencyCode").ascending())))
                            .map(AgencyEntity::toDomain)
                            .getContent();
                    return Flux.fromIterable(agencies);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsAnotherActive(String subtypeCode, String excludeAgencyCode) {
        return Mono.fromCallable(() -> repository.existsAnotherActive(subtypeCode, excludeAgencyCode))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
