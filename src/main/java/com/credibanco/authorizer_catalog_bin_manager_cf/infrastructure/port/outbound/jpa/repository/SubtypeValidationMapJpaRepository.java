package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeValidationMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SubtypeValidationMapJpaRepository extends JpaRepository<SubtypeValidationMapEntity, Long>, JpaSpecificationExecutor<SubtypeValidationMapEntity> {

    boolean existsBySubtypeCodeAndBinAndValidationIdAndStatus(String subtypeCode, String bin, Long validationId, String status);

    Optional<SubtypeValidationMapEntity> findBySubtypeCodeAndBinAndValidationId(String subtypeCode, String bin, Long validationId);
}
