package com.msp.sales_purchase_service.dto;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateSaleResponse {
    private Integer orderId;
    private String orderNumber;
    private String status;
}

