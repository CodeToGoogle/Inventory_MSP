package com.msp.product_service.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductResponse {
    private Integer productID;
    private String productName;
    private String productType;
    private Integer productCategoryID;
    private Integer unitID;
    private BigDecimal qtyOnHand;
    private BigDecimal reorderLevel;
    private BigDecimal safetyStock;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private String sku;
    private String barcode;
    private Boolean isActive;
}
