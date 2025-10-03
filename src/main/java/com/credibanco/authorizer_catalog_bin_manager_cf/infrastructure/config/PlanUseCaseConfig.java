package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.config;

import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.inbound.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.CommercePlanItemRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.CommercePlanRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.SubtypePlanRepository;
import com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.use_case.*;
import com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.util.BlockingTransactionExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlanUseCaseConfig {

    @Bean CreatePlanUseCase createPlanUseCase(CommercePlanRepository r, BlockingTransactionExecutor txExecutor) {
        return new CreatePlanService(r, txExecutor);
    }

    @Bean AddPlanItemUseCase addPlanItemUseCase(CommercePlanRepository pr,
                                                CommercePlanItemRepository ir,
                                                BlockingTransactionExecutor txExecutor) {
        return new AddPlanItemService(pr, ir, txExecutor);
    }
    @Bean
    AssignPlanToSubtypeUseCase assignPlanToSubtypeUseCase(CommercePlanRepository pr,
                                                          SubtypePlanRepository sr,
                                                          com.credibanco.authorizer_catalog_bin_manager_cf.application.plan.port.outbound.SubtypeReadOnlyRepository subtypeRepo,
                                                          CommercePlanItemRepository itemRepo,
                                                          BlockingTransactionExecutor txExecutor) {
        return new AssignPlanToSubtypeService(pr, sr, subtypeRepo, itemRepo, txExecutor);
    }

    @Bean GetPlanUseCase getPlanUseCase(CommercePlanRepository r) { return new GetPlanService(r); }
    @Bean ListPlansUseCase listPlansUseCase(CommercePlanRepository r) { return new ListPlansService(r); }
    @Bean UpdatePlanUseCase updatePlanUseCase(CommercePlanRepository r) { return new UpdatePlanService(r); }
    @Bean ChangePlanStatusUseCase changePlanStatusUseCase(CommercePlanRepository r) { return new ChangePlanStatusService(r); }
    @Bean ListPlanItemsUseCase listPlanItemsUseCase(CommercePlanRepository pr, CommercePlanItemRepository ir) { return new ListPlanItemsService(pr, ir); }

    @Bean
    public ChangePlanItemStatusUseCase changePlanItemStatusUseCase(
            CommercePlanRepository pr,
            CommercePlanItemRepository ir,
            BlockingTransactionExecutor txExecutor
    ) {
        return new ChangePlanItemStatusService(pr, ir, txExecutor);
    }
}
