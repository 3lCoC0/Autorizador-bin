package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.config;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.inbound.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.use_case.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.bin.port.outbound.BinRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinUseCaseConfig {

    @Bean
    GetBinUseCase getBinUseCase(BinRepository repo) { return new GetBinService(repo); }
    @Bean
    ListBinsUseCase listBinsUseCase(BinRepository repo) { return new ListBinsService(repo); }
    @Bean public CreateBinUseCase createBinUseCase(BinRepository repo, BlockingTransactionExecutor txExecutor) { return new CreateBinService(repo, txExecutor); }
    @Bean public ChangeBinStatusUseCase changeBinStatusUseCase(BinRepository repo, BlockingTransactionExecutor txExecutor) { return new ChangeBinStatusService(repo, txExecutor); }
    @Bean public UpdateBinUseCase updateBinUseCase(BinRepository repo, BlockingTransactionExecutor txExecutor) { return new UpdateBinService(repo, txExecutor); }

}
