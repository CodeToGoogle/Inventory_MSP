package com.msp.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductRequest {
    @NotBlank
    private String productName;

    @NotBlank
    private String productType;

    private Integer productCategoryID;

    @NotNull
    private Integer unitID;

    @PositiveOrZero
    private BigDecimal qtyOnHand;

    private BigDecimal reorderLevel;
    private BigDecimal safetyStock;

    private BigDecimal costPrice;
    private BigDecimal sellingPrice;

    private String sku;
    private String barcode;
}
