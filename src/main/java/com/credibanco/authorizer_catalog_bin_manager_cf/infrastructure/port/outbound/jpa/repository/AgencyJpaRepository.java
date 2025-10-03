package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgencyJpaRepository extends JpaRepository<AgencyEntity, AgencyEntityId>, JpaSpecificationExecutor<AgencyEntity> {

    long countByIdSubtypeCodeAndStatus(String subtypeCode, String status);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AgencyEntity a WHERE a.id.subtypeCode = :subtypeCode AND a.status = 'A' AND a.id.agencyCode <> :exclude")
    boolean existsAnotherActive(@Param("subtypeCode") String subtypeCode, @Param("exclude") String excludeAgencyCode);
}
