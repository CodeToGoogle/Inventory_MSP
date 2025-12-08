package com.msp.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductUnits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductUnit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer unitID;

    @Column(nullable = false, unique = true)
    private String unitName;

    private Double contains;
    private Integer referenceUnitID;
    private Double conversionFactor;
    private Boolean isBaseUnit;
}

