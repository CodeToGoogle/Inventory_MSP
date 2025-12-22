package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="BillOfMaterialsLine")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BillOfMaterialsLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bomLineID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bomID")
    private BillOfMaterials billOfMaterials;

    private Integer productID; // component product id (raw material)
    @Column(precision=15,scale=3)
    private BigDecimal qty;
    private Integer unitID;
    @Column(precision=5,scale=2)
    private BigDecimal scrapFactor;
}
