package com.msp.sales_purchase_service.dto;


import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GrnRequest {
    @NotNull private Integer receivedBy;
    @NotNull private List<GrnLine> lines;
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GrnLine { @NotNull private Integer lineId; @NotNull private BigDecimal qty; private String batchNumber; }
}

