package com.msp.inventory_manufacturing_service.dto;



import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateMORequest {
    private Integer referenceSo; // optional
    private Integer productId;   // FG product
    private BigDecimal totalQty;
    private Integer unitId;
}

