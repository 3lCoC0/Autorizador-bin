package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository;

import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubtypeJpaRepository extends JpaRepository<SubtypeEntity, SubtypeEntityId>, JpaSpecificationExecutor<SubtypeEntity> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SubtypeEntity s WHERE s.id.bin = :bin AND s.binExt = :binExt")
    boolean existsByBinAndBinExt(@Param("bin") String bin, @Param("binExt") String binExt);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SubtypeEntity s WHERE s.id.subtypeCode = :code AND s.status = 'A'")
    boolean existsActiveBySubtypeCode(@Param("code") String subtypeCode);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SubtypeEntity s WHERE s.id.subtypeCode = :code")
    boolean existsBySubtypeCode(@Param("code") String subtypeCode);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SubtypeEntity s WHERE s.id.subtypeCode = :code AND s.id.bin = :bin")
    boolean existsBySubtypeCodeAndBin(@Param("code") String code, @Param("bin") String bin);
}
