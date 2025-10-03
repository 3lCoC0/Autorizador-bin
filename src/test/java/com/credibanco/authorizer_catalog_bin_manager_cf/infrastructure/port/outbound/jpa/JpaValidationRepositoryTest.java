package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.Validation;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationDataType;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeValidationEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeValidationJpaRepository;
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
class JpaValidationRepositoryTest {

    @Autowired
    private SubtypeValidationJpaRepository jpaRepository;

    @Autowired
    private BlockingExecutor blockingExecutor;

    private JpaValidationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaValidationRepository(jpaRepository, blockingExecutor);
    }

    @Test
    void findAllWithStatusFilterReturnsOnlyActiveValidations() {
        SubtypeValidationEntity active = new SubtypeValidationEntity();
        active.setCode("VAL-A");
        active.setDescription("Active validation");
        active.setDataType(ValidationDataType.TEXT);
        active.setStatus("A");
        active.setCreatedAt(OffsetDateTime.now());
        active.setUpdatedAt(OffsetDateTime.now());

        SubtypeValidationEntity inactive = new SubtypeValidationEntity();
        inactive.setCode("VAL-I");
        inactive.setDescription("Inactive validation");
        inactive.setDataType(ValidationDataType.TEXT);
        inactive.setStatus("I");
        inactive.setCreatedAt(OffsetDateTime.now());
        inactive.setUpdatedAt(OffsetDateTime.now());

        jpaRepository.save(active);
        jpaRepository.save(inactive);

        List<Validation> results = repository.findAll("A", null, 0, 10)
                .collectList()
                .block();

        assertThat(results)
                .extracting(Validation::status)
                .containsOnly("A");
        assertThat(results)
                .extracting(Validation::code)
                .containsExactly("VAL-A");
    }
}
