package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.CommercePlanItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommercePlanItemJpaRepository extends JpaRepository<CommercePlanItemEntity, Long>, JpaSpecificationExecutor<CommercePlanItemEntity> {

    Optional<CommercePlanItemEntity> findFirstByPlanIdAndMccOrderByPlanItemIdDesc(Long planId, String mcc);

    Optional<CommercePlanItemEntity> findFirstByPlanIdAndMerchantIdOrderByPlanItemIdDesc(Long planId, String merchantId);

    @Query("SELECT c FROM CommercePlanItemEntity c WHERE c.planId = :planId AND (c.mcc = :value OR c.merchantId = :value)")
    Page<CommercePlanItemEntity> findByPlanIdAndValue(@Param("planId") Long planId, @Param("value") String value, Pageable pageable);

    @Query("SELECT COALESCE(c.mcc, c.merchantId) FROM CommercePlanItemEntity c WHERE c.planId = :planId AND ((c.mcc IS NOT NULL AND c.mcc IN :values) OR (c.merchantId IS NOT NULL AND c.merchantId IN :values))")
    List<String> findExistingValues(@Param("planId") Long planId, @Param("values") Collection<String> values);

    boolean existsByPlanIdAndStatus(Long planId, String status);
}
