package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.SubtypePlanLink;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "SUBTYPE_COMMERCE_PLAN")
public class SubtypePlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subtype_plan_seq")
    @SequenceGenerator(name = "subtype_plan_seq", sequenceName = "SEQ_SUBTYPE_COMMERCE_PLAN_ID", allocationSize = 1)
    @Column(name = "SUBTYPE_PLAN_ID")
    private Long subtypePlanId;

    @Column(name = "SUBTYPE_CODE", nullable = false)
    private String subtypeCode;

    @Column(name = "PLAN_ID", nullable = false)
    private Long planId;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    public SubtypePlanLink toDomain() {
        return SubtypePlanLink.rehydrate(subtypeCode, planId, createdAt, updatedAt, updatedBy);
    }
}
