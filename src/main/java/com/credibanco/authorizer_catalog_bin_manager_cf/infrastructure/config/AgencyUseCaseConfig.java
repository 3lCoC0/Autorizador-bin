package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.config;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.inbound.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.outbound.AgencyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.port.outbound.SubtypeReadOnlyRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.agency.use_case.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgencyUseCaseConfig {

    @Bean
    public CreateAgencyUseCase createAgencyUseCase(
            AgencyRepository repo, SubtypeReadOnlyRepository subtypeRepo, BlockingTransactionExecutor txExecutor
    ) { return new CreateAgencyService(repo, subtypeRepo, txExecutor); }

    @Bean
    public UpdateAgencyUseCase updateAgencyUseCase(
            AgencyRepository repo,
            SubtypeReadOnlyRepository subtypeRepo,
            BlockingTransactionExecutor txExecutor
    ) {
        return new UpdateAgencyService(repo, subtypeRepo, txExecutor);
    }


    @Bean
    public ChangeAgencyStatusUseCase changeAgencyStatusUseCase(
            AgencyRepository repo, SubtypeReadOnlyRepository subtypeRepo, BlockingTransactionExecutor txExecutor
    ) { return new ChangeAgencyStatusService(repo, subtypeRepo, txExecutor); }

    @Bean
    public GetAgencyUseCase getAgencyUseCase(
            AgencyRepository repo,
            SubtypeReadOnlyRepository subtypeRepo
    ) {
        return new GetAgencyService(repo, subtypeRepo);
    }


    @Bean
    public ListAgenciesUseCase listAgenciesUseCase(
            AgencyRepository repo,
            SubtypeReadOnlyRepository subtypeRepo
    ) {
        return new ListAgenciesService(repo, subtypeRepo);
    }
}
