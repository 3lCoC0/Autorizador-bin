package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.agency.Agency;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.AgencyEntityId;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.AgencyJpaRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BlockingExecutor.class)
class JpaAgencyRepositoryTest {

    @Autowired
    private AgencyJpaRepository jpaRepository;

    @Autowired
    private BlockingExecutor blockingExecutor;

    private JpaAgencyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaAgencyRepository(jpaRepository, blockingExecutor);
    }

    @Test
    void findAllWithSubtypeFilterReturnsOnlyMatchingAgencies() {
        AgencyEntity subtypeA = new AgencyEntity();
        subtypeA.setId(new AgencyEntityId("SUB", "AG1"));
        subtypeA.setName("Agency One");
        subtypeA.setStatus("A");
        subtypeA.setCreatedAt(OffsetDateTime.now());
        subtypeA.setUpdatedAt(OffsetDateTime.now());
        subtypeA.setUpdatedBy("tester");

        AgencyEntity subtypeB = new AgencyEntity();
        subtypeB.setId(new AgencyEntityId("OTHER", "AG2"));
        subtypeB.setName("Agency Two");
        subtypeB.setStatus("A");
        subtypeB.setCreatedAt(OffsetDateTime.now());
        subtypeB.setUpdatedAt(OffsetDateTime.now());
        subtypeB.setUpdatedBy("tester");

        jpaRepository.save(subtypeA);
        jpaRepository.save(subtypeB);

        List<Agency> results = repository.findAll("SUB", null, null, 0, 10)
                .collectList()
                .block();

        assertThat(results)
                .extracting(Agency::subtypeCode)
                .containsOnly("SUB");
        assertThat(results)
                .extracting(Agency::agencyCode)
                .containsExactly("AG1");
    }
}
