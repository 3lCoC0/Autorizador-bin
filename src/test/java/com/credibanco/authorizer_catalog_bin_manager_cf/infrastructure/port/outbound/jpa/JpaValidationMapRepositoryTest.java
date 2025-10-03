package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationMap;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeValidationMapEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeValidationMapJpaRepository;
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
class JpaValidationMapRepositoryTest {

    @Autowired
    private SubtypeValidationMapJpaRepository jpaRepository;

    @Autowired
    private BlockingExecutor blockingExecutor;

    private JpaValidationMapRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaValidationMapRepository(jpaRepository, blockingExecutor);
    }

    @Test
    void findAllWithSubtypeFilterReturnsOnlyMatchingValidationMaps() {
        SubtypeValidationMapEntity first = new SubtypeValidationMapEntity();
        first.setSubtypeCode("SUB");
        first.setBin("123456");
        first.setValidationId(1L);
        first.setStatus("A");
        first.setCreatedAt(OffsetDateTime.now());
        first.setUpdatedAt(OffsetDateTime.now());
        first.setUpdatedBy("tester");

        SubtypeValidationMapEntity second = new SubtypeValidationMapEntity();
        second.setSubtypeCode("OTHER");
        second.setBin("123456");
        second.setValidationId(2L);
        second.setStatus("A");
        second.setCreatedAt(OffsetDateTime.now());
        second.setUpdatedAt(OffsetDateTime.now());
        second.setUpdatedBy("tester");

        jpaRepository.save(first);
        jpaRepository.save(second);

        List<ValidationMap> results = repository.findAll("SUB", null, null, 0, 10)
                .collectList()
                .block();

        assertThat(results)
                .extracting(ValidationMap::subtypeCode)
                .containsOnly("SUB");
        assertThat(results)
                .extracting(ValidationMap::validationId)
                .containsExactly(1L);
    }
}
