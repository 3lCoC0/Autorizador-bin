package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommercePlan;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommerceValidationMode;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.CommercePlanEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.CommercePlanJpaRepository;
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
class JpaCommercePlanRepositoryTest {

    @Autowired
    private CommercePlanJpaRepository jpaRepository;

    @Autowired
    private BlockingExecutor blockingExecutor;

    private JpaCommercePlanRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaCommercePlanRepository(jpaRepository, blockingExecutor);
    }

    @Test
    void findAllWithStatusFilterReturnsOnlyMatchingPlans() {
        CommercePlanEntity active = new CommercePlanEntity();
        active.setPlanCode("PLAN-A");
        active.setPlanName("Plan Active");
        active.setValidationMode(CommerceValidationMode.MERCHANT_ID);
        active.setDescription("Active plan");
        active.setStatus("A");
        active.setCreatedAt(OffsetDateTime.now());
        active.setUpdatedAt(OffsetDateTime.now());

        CommercePlanEntity inactive = new CommercePlanEntity();
        inactive.setPlanCode("PLAN-I");
        inactive.setPlanName("Plan Inactive");
        inactive.setValidationMode(CommerceValidationMode.MCC);
        inactive.setDescription("Inactive plan");
        inactive.setStatus("I");
        inactive.setCreatedAt(OffsetDateTime.now());
        inactive.setUpdatedAt(OffsetDateTime.now());

        jpaRepository.save(active);
        jpaRepository.save(inactive);

        List<CommercePlan> results = repository.findAll("A", null, 0, 10)
                .collectList()
                .block();

        assertThat(results)
                .extracting(CommercePlan::status)
                .containsOnly("A");
        assertThat(results)
                .extracting(CommercePlan::code)
                .containsExactly("PLAN-A");
    }
}
