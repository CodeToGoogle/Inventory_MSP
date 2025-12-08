package com.msp.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ProductMaster")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productID;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String productType; // Raw Material, Finished Good, etc.

    private Integer productCategoryID;

    private Integer unitID;

    @Column(precision = 15, scale = 3)
    private BigDecimal qtyOnHand;

    @Column(precision = 15, scale = 3)
    private BigDecimal reorderLevel;

    @Column(precision = 15, scale = 3)
    private BigDecimal safetyStock;

    @Column(precision = 19, scale = 4)
    private BigDecimal costPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal sellingPrice;

    @Column(unique = true)
    private String sku;

    @Column(unique = true)
    private String barcode;

    private Boolean isActive = true;
}
