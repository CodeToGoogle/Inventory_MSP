package com.msp.product_service.dto;


import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnitDto {
    private Integer unitID;

    @NotBlank
    private String unitName;

    private Double contains;
    private Integer referenceUnitID;
    private Double conversionFactor;
    private Boolean isBaseUnit;
}

