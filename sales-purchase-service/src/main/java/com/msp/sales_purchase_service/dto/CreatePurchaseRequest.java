package com.msp.sales_purchase_service.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreatePurchaseRequest {
    @NotNull private Integer vendorId;
    @NotNull private List<PurchaseLine> lines;
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PurchaseLine { @NotNull private Integer productId; @NotNull private BigDecimal orderedQty; @NotNull private BigDecimal purchaseUnitPrice; @NotNull private Integer unitId; }
}

