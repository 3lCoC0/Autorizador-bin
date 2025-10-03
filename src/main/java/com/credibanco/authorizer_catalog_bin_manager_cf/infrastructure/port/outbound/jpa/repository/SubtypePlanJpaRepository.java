package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubtypePlanJpaRepository extends JpaRepository<SubtypePlanEntity, Long> {

    Optional<SubtypePlanEntity> findBySubtypeCode(String subtypeCode);
}
