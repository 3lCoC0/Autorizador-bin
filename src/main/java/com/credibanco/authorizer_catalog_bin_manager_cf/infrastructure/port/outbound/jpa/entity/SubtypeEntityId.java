package com.credibanco.authorizer_catalog_bin_manager_cf.infrastructure.port.outbound.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class SubtypeEntityId implements Serializable {

    @Column(name = "BIN", nullable = false, length = 9)
    private String bin;

    @Column(name = "SUBTYPE_CODE", nullable = false, length = 3)
    private String subtypeCode;
}
