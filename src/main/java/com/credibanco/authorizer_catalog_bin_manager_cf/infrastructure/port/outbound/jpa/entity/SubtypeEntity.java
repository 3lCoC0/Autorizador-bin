package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import com.credibanco.authorizer_catalog_bin_manager_cf.domain.subtype.Subtype;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SUBTYPE")
public class SubtypeEntity {

    @EmbeddedId
    private SubtypeEntityId id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;

    @Column(name = "OWNER_ID_TYPE")
    private String ownerIdType;

    @Column(name = "OWNER_ID_NUMBER")
    private String ownerIdNumber;

    @Column(name = "BIN_EXT")
    private String binExt;

    @Column(name = "BIN_EFECTIVO")
    private String binEfectivo;

    @Column(name = "SUBTYPE_ID", insertable = false, updatable = false)
    private Long subtypeId;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private OffsetDateTime updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    public static SubtypeEntity fromDomain(Subtype subtype) {
        SubtypeEntity entity = new SubtypeEntity();
        entity.setId(new SubtypeEntityId(subtype.bin(), subtype.subtypeCode()));
        entity.setName(subtype.name());
        entity.setDescription(subtype.description());
        entity.setStatus(subtype.status());
        entity.setOwnerIdType(subtype.ownerIdType());
        entity.setOwnerIdNumber(subtype.ownerIdNumber());
        entity.setBinExt(subtype.binExt());
        entity.setBinEfectivo(subtype.binEfectivo());
        entity.setSubtypeId(subtype.subtypeId());
        entity.setCreatedAt(subtype.createdAt());
        entity.setUpdatedAt(subtype.updatedAt());
        entity.setUpdatedBy(subtype.updatedBy());
        return entity;
    }

    public Subtype toDomain() {
        return Subtype.rehydrate(
                id.getSubtypeCode(),
                id.getBin(),
                name,
                description,
                status,
                ownerIdType,
                ownerIdNumber,
                binExt,
                binEfectivo,
                subtypeId,
                createdAt,
                updatedAt,
                updatedBy
        );
    }
}
