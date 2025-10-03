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
public class AgencyEntityId implements Serializable {

    @Column(name = "SUBTYPE_CODE", nullable = false, length = 3)
    private String subtypeCode;

    @Column(name = "AGENCY_CODE", nullable = false, length = 20)
    private String agencyCode;
}
