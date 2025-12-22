package com.msp.inventory_manufacturing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLevelsResponse {
    private Integer productId;
    private BigDecimal onHandQty;
    private BigDecimal availableQty;
    private BigDecimal reservedQty;
}
