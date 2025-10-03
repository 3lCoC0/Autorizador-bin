package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommercePlan;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.CommerceValidationMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "COMMERCE_PLAN")
public class CommercePlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commerce_plan_seq")
    @SequenceGenerator(name = "commerce_plan_seq", sequenceName = "SEQ_COMMERCE_PLAN_ID", allocationSize = 1)
    @Column(name = "PLAN_ID")
    private Long planId;

    @Column(name = "PLAN_CODE", nullable = false, unique = true)
    private String planCode;

    @Column(name = "PLAN_NAME", nullable = false)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(name = "VALIDATION_MODE", nullable = false)
    private CommerceValidationMode validationMode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    public void updateFromDomain(CommercePlan plan) {
        this.planCode = plan.code();
        this.planName = plan.name();
        this.validationMode = plan.validationMode();
        this.description = plan.description();
        this.status = plan.status();
        this.createdAt = plan.createdAt();
        this.updatedAt = plan.updatedAt();
        this.updatedBy = plan.updatedBy();
        this.planId = plan.planId();
    }

    public CommercePlan toDomain() {
        return CommercePlan.rehydrate(
                planId,
                planCode,
                planName,
                validationMode,
                description,
                status,
                createdAt,
                updatedAt,
                updatedBy
        );
    }
}
