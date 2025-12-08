package com.msp.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductCategories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @Column(nullable = false, unique = true)
    private String categoryName;

    private Integer parentCategoryID;
    @Column(columnDefinition = "TEXT")
    private String description;
}
