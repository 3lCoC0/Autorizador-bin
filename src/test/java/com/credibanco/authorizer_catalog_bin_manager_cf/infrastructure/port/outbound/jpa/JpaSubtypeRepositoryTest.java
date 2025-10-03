package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.subtype.Subtype;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeEntity;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity.SubtypeEntityId;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.repository.SubtypeJpaRepository;
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
class JpaSubtypeRepositoryTest {

    @Autowired
    private SubtypeJpaRepository jpaRepository;

    @Autowired
    private BlockingExecutor blockingExecutor;

    private JpaSubtypeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JpaSubtypeRepository(jpaRepository, blockingExecutor);
    }

    @Test
    void findAllWithBinFilterReturnsOnlyMatchingSubtypes() {
        SubtypeEntity first = new SubtypeEntity();
        first.setId(new SubtypeEntityId("123456", "001"));
        first.setName("Subtype One");
        first.setDescription("Subtype for bin 123456");
        first.setStatus("A");
        first.setOwnerIdType("CC");
        first.setOwnerIdNumber("123456789");
        first.setBinExt(null);
        first.setBinEfectivo("123456");
        first.setSubtypeId(1L);
        first.setCreatedAt(OffsetDateTime.now());
        first.setUpdatedAt(OffsetDateTime.now());
        first.setUpdatedBy("tester");

        SubtypeEntity second = new SubtypeEntity();
        second.setId(new SubtypeEntityId("654321", "002"));
        second.setName("Subtype Two");
        second.setDescription("Subtype for bin 654321");
        second.setStatus("A");
        second.setOwnerIdType("CC");
        second.setOwnerIdNumber("987654321");
        second.setBinExt(null);
        second.setBinEfectivo("654321");
        second.setSubtypeId(2L);
        second.setCreatedAt(OffsetDateTime.now());
        second.setUpdatedAt(OffsetDateTime.now());
        second.setUpdatedBy("tester");

        jpaRepository.save(first);
        jpaRepository.save(second);

        List<Subtype> results = repository.findAll("123456", null, null, 0, 10)
                .collectList()
                .block();

        assertThat(results)
                .extracting(Subtype::bin)
                .containsOnly("123456");
        assertThat(results)
                .extracting(Subtype::subtypeCode)
                .containsExactly("001");
    }
}
