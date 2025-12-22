package com.msp.inventory_manufacturing_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="ProductMaster")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductMaster {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @Column(name = "ProductName")
    private String productName;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "ProductType")
    private String productType;

    @Column(name = "CategoryID") // Corrected from ProductCategoryID
    private Integer categoryID;

    @Column(name = "BaseUnitID")
    private Integer baseUnitID;

    @Column(precision=15,scale=3)
    @Builder.Default
    private BigDecimal qtyOnHand = BigDecimal.ZERO;

    @Column(precision=15,scale=3)
    @Builder.Default
    private BigDecimal reservedQty = BigDecimal.ZERO;

    @Column(precision=15,scale=2)
    private BigDecimal costPrice;

    @Column(precision=15,scale=2)
    private BigDecimal sellingPrice;

    private BigDecimal reorderLevel;
    private BigDecimal safetyStock;

    @Builder.Default
    private Boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist public void prePersist(){ createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}
