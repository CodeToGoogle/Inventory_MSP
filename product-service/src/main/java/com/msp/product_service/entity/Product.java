package com.msp.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductMaster")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productID;
    private String productName;
    private String productType;
    private Integer productCategoryID;
    private Integer unitID;
    private Double qtyOnHand;
    private Boolean isActive;
    private String sku;
}

