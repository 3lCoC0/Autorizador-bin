package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.config;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.inbound.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.AgencyReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.BinReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.IdTypeReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.port.outbound.SubtypeRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.subtype.use_case.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubtypeUseCaseConfig {

    @Bean
    public CreateSubtypeUseCase createSubtypeUseCase(
            SubtypeRepository repo,
            BinReadOnlyRepository binRepo,
            IdTypeReadOnlyRepository idTypeRepo,
            BlockingTransactionExecutor txExecutor
    ) {
        return new CreateSubtypeService(repo, binRepo, idTypeRepo, txExecutor);
    }

    @Bean
    public UpdateSubtypeBasicsUseCase updateSubtypeBasicsUseCase(
            SubtypeRepository repo, BinReadOnlyRepository binRepo, BlockingTransactionExecutor txExecutor
    ) {
        return new UpdateSubtypeBasicsService(repo, binRepo, txExecutor);
    }
    @Bean
    public ChangeSubtypeStatusUseCase changeSubtypeStatusUseCase(
            SubtypeRepository repo,
            AgencyReadOnlyRepository agencyRepo,
            BlockingTransactionExecutor txExecutor
    ) {
        return new ChangeSubtypeStatusService(repo, agencyRepo, txExecutor);
    }

    @Bean
    public GetSubtypeUseCase getSubtypeUseCase(SubtypeRepository repo) {
        return new GetSubtypeService(repo);
    }

    @Bean
    public ListSubtypesUseCase listSubtypesUseCase(
            SubtypeRepository repo,
            BinReadOnlyRepository binRepo
    ) {
        return new ListSubtypesService(repo, binRepo);
    }
}
