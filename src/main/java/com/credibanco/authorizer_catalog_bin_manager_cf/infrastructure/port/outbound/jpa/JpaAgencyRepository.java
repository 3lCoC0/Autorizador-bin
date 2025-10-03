package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.outbound.AgencyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.agency.Agency;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntityId;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.AgencyJpaRepository;
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
public class JpaAgencyRepository implements AgencyRepository {

    private final AgencyJpaRepository repository;
    private final BlockingExecutor blockingExecutor;

    @Override
    public Mono<Boolean> existsByPk(String subtypeCode, String agencyCode) {
        return blockingExecutor.mono(() -> repository.existsById(new AgencyEntityId(subtypeCode, agencyCode)));
    }

    @Override
    public Mono<Agency> save(Agency aggregate) {
        return blockingExecutor.mono(() -> {
            AgencyEntityId id = new AgencyEntityId(aggregate.subtypeCode(), aggregate.agencyCode());
            repository.save(AgencyEntity.fromDomain(aggregate));
            return repository.findById(id).map(AgencyEntity::toDomain).orElseThrow();
        });
    }

    @Override
    public Mono<Agency> findByPk(String subtypeCode, String agencyCode) {
        return blockingExecutor.mono(() -> repository.findById(new AgencyEntityId(subtypeCode, agencyCode))
                .map(AgencyEntity::toDomain)
                .orElse(null));
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
        return blockingExecutor.flux(() -> {
            List<Agency> agencies = repository.findAll(finalSpec,
                            PageRequest.of(p, s, Sort.by("id.subtypeCode").ascending().and(Sort.by("id.agencyCode").ascending())))
                    .map(AgencyEntity::toDomain)
                    .getContent();
            return agencies;
        });
    }

    @Override
    public Mono<Boolean> existsAnotherActive(String subtypeCode, String excludeAgencyCode) {
        return blockingExecutor.mono(() -> repository.existsAnotherActive(subtypeCode, excludeAgencyCode));
    }
}
