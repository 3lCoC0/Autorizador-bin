package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeValidationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SubtypeValidationJpaRepository extends JpaRepository<SubtypeValidationEntity, Long>, JpaSpecificationExecutor<SubtypeValidationEntity> {

    boolean existsByCode(String code);

    Optional<SubtypeValidationEntity> findByCode(String code);
}
