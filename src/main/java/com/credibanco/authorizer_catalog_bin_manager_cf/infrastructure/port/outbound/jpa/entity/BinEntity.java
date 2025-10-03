package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.bin.Bin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "BIN")
public class BinEntity {

    @Id
    @Column(name = "BIN", nullable = false, length = 9)
    private String bin;

    @Column(name = "NAME", nullable = false, length = 120)
    private String name;

    @Column(name = "TYPE_BIN", nullable = false, length = 20)
    private String typeBin;

    @Column(name = "TYPE_ACCOUNT", nullable = false, length = 10)
    private String typeAccount;

    @Column(name = "COMPENSATION_COD")
    private String compensationCod;

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

    @Column(name = "USES_BIN_EXT", nullable = false, length = 1)
    private String usesBinExt;

    @Column(name = "BIN_EXT_DIGITS")
    private Integer binExtDigits;

    public static BinEntity fromDomain(Bin bin) {
        BinEntity entity = new BinEntity();
        entity.setBin(bin.bin());
        entity.setName(bin.name());
        entity.setTypeBin(bin.typeBin());
        entity.setTypeAccount(bin.typeAccount());
        entity.setCompensationCod(bin.compensationCod());
        entity.setDescription(bin.description());
        entity.setStatus(bin.status());
        entity.setCreatedAt(bin.createdAt());
        entity.setUpdatedAt(bin.updatedAt());
        entity.setUpdatedBy(bin.updatedBy());
        entity.setUsesBinExt(bin.usesBinExt());
        entity.setBinExtDigits(bin.binExtDigits());
        return entity;
    }

    public Bin toDomain() {
        return Bin.rehydrate(
                bin,
                name,
                typeBin,
                typeAccount,
                compensationCod,
                description,
                status,
                createdAt,
                updatedAt,
                updatedBy,
                usesBinExt,
                binExtDigits
        );
    }
}
