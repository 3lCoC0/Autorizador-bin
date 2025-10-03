package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationMap;
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
@Table(name = "SUBTYPE_VALIDATION_MAP")
public class SubtypeValidationMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subtype_val_map_seq")
    @SequenceGenerator(name = "subtype_val_map_seq", sequenceName = "SEQ_SUBTYPE_VAL_MAP_ID", allocationSize = 1)
    @Column(name = "SUBTYPE_VAL_MAP_ID")
    private Long mapId;

    @Column(name = "SUBTYPE_CODE", nullable = false)
    private String subtypeCode;

    @Column(name = "BIN", nullable = false)
    private String bin;

    @Column(name = "VALIDATION_ID", nullable = false)
    private Long validationId;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    @Column(name = "VALUE_FLAG")
    private String valueFlag;

    @Column(name = "VALUE_NUM")
    private Double valueNum;

    @Column(name = "VALUE_TEXT")
    private String valueText;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    public void updateFromDomain(ValidationMap map) {
        this.mapId = map.mapId();
        this.subtypeCode = map.subtypeCode();
        this.bin = map.bin();
        this.validationId = map.validationId();
        this.status = map.status();
        this.valueFlag = map.valueFlag();
        this.valueNum = map.valueNum();
        this.valueText = map.valueText();
        this.createdAt = map.createdAt();
        this.updatedAt = map.updatedAt() != null ? map.updatedAt() : map.createdAt();
        this.updatedBy = map.updatedBy();
    }

    public ValidationMap toDomain() {
        return ValidationMap.rehydrate(
                mapId,
                subtypeCode,
                bin,
                validationId,
                status,
                valueFlag,
                valueNum,
                valueText,
                createdAt,
                updatedAt,
                updatedBy
        );
    }
}
