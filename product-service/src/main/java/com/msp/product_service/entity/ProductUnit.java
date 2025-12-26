package com.msp.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ProductUnits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UnitID")
    private Integer unitID;

    @Column(name = "UnitName", nullable = false, unique = true)
    private String unitName;

    @Column(name = "Contains", nullable = false)
    private BigDecimal contains;

    @Column(name = "ReferenceUnitID")
    private Integer referenceUnitID;

    @Column(name = "ConversionFactor")
    private BigDecimal conversionFactor;

    @Column(name = "IsBaseUnit")
    private Boolean isBaseUnit;
}
