package com.msp.sales_purchase_service.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryRequest {
    @NotNull private Integer locationId;
    private Integer deliveryMethodId;
    @NotNull private List<DeliveryLine> lines;
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DeliveryLine { @NotNull private Integer lineId; @NotNull private BigDecimal qty; }
}

