package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.plan.PlanItem;
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
@Table(name = "COMMERCE_PLAN_ITEM")
public class CommercePlanItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commerce_plan_item_seq")
    @SequenceGenerator(name = "commerce_plan_item_seq", sequenceName = "SEQ_COMMERCE_PLAN_ITEM_ID", allocationSize = 1)
    @Column(name = "PLAN_ITEM_ID")
    private Long planItemId;

    @Column(name = "PLAN_ID", nullable = false)
    private Long planId;

    @Column(name = "MCC")
    private String mcc;

    @Column(name = "MERCHANT_ID")
    private String merchantId;

    @Column(name = "STATUS", length = 1)
    private String status;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    public static CommercePlanItemEntity newMcc(Long planId, String mcc, String by, OffsetDateTime now) {
        CommercePlanItemEntity entity = new CommercePlanItemEntity();
        entity.setPlanId(planId);
        entity.setMcc(mcc);
        entity.setMerchantId(null);
        entity.setStatus("A");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(by);
        return entity;
    }

    public static CommercePlanItemEntity newMerchant(Long planId, String merchantId, String by, OffsetDateTime now) {
        CommercePlanItemEntity entity = new CommercePlanItemEntity();
        entity.setPlanId(planId);
        entity.setMcc(null);
        entity.setMerchantId(merchantId);
        entity.setStatus("A");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(by);
        return entity;
    }

    public PlanItem toDomain() {
        return PlanItem.rehydrate(
                planItemId,
                planId,
                mcc != null ? mcc : merchantId,
                createdAt,
                updatedAt,
                updatedBy,
                status
        );
    }
}
