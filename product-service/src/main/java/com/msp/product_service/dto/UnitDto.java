package com.msp.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDto {
    private Integer unitID;
    private String unitName;
    private BigDecimal contains;
    private Integer referenceUnitID;
    private BigDecimal conversionFactor;
    private Boolean isBaseUnit;
}
