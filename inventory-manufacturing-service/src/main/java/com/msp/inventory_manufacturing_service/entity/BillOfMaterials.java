package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="BillOfMaterials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BillOfMaterials {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bomID;
    private String bomName;
    @Column(name = "ProductID")
    private Integer productId;
    private String version;
    private Boolean isActive = true;
    private LocalDate effectiveDate;

    @OneToMany(mappedBy = "billOfMaterials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillOfMaterialsLine> lines;
}
