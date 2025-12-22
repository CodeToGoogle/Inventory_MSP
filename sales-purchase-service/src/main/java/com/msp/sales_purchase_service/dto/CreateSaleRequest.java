package com.msp.sales_purchase_service.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateSaleRequest {
    @NotNull private Integer customerId;
    @NotNull private List<SaleLine> lines;
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SaleLine {
        @NotNull private Integer productId;
        @NotNull private Integer unitId;
        @NotNull private BigDecimal orderedQty;
        @NotNull private BigDecimal saleUnitPrice;
    }
}

