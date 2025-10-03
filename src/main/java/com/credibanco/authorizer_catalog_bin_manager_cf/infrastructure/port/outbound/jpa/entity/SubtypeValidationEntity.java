package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.Validation;
import com.credibanco.authorizer_catalog_bin_manager_cf.domain.rule.ValidationDataType;
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
@Table(name = "SUBTYPE_VALIDATION")
public class SubtypeValidationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subtype_validation_seq")
    @SequenceGenerator(name = "subtype_validation_seq", sequenceName = "SEQ_SUBTIPO_VALIDATION_ID", allocationSize = 1)
    @Column(name = "VALIDATION_ID")
    private Long validationId;

    @Column(name = "CODE", nullable = false, unique = true)
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_TYPE", nullable = false)
    private ValidationDataType dataType;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    @Column(name = "VALID_FROM")
    private OffsetDateTime validFrom;

    @Column(name = "VALID_TO")
    private OffsetDateTime validTo;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    public void updateFromDomain(Validation validation) {
        this.validationId = validation.validationId();
        this.code = validation.code();
        this.description = validation.description();
        this.dataType = validation.dataType();
        this.status = validation.status();
        this.validFrom = validation.validFrom();
        this.validTo = validation.validTo();
        this.createdAt = validation.createdAt();
        this.updatedAt = validation.updatedAt();
        this.updatedBy = validation.updatedBy();
    }

    public Validation toDomain() {
        return Validation.rehydrate(
                validationId,
                code,
                description,
                dataType,
                status,
                validFrom,
                validTo,
                createdAt,
                updatedAt,
                updatedBy
        );
    }
}
