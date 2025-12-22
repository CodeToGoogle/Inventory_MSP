package com.msp.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ProductMaster")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "SKU", unique = true)
    private String sku;

    // Removed Description field as it does not exist in the V1 database schema

    @Column(name = "ProductCategoryID") // Corrected column mapping
    private Integer productCategoryId;

    @Column(name = "UnitID")
    private Integer unitId;

    @Column(name = "CostPrice")
    private BigDecimal costPrice;

    @Column(name = "SellingPrice")
    private BigDecimal sellingPrice;

    @Column(name = "ReorderLevel")
    private BigDecimal reorderLevel;

    @Column(name = "SafetyStock")
    private BigDecimal safetyStock;

    @Column(name = "IsActive")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "ProductType", nullable = false)
    private String productType;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
