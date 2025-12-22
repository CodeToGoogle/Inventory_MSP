package com.msp.sales_purchase_service.dto;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PendingSummaryDto {
    private int sales_orders_pending_approval;
    private int deliveries_pending;
    private int purchase_orders_pending_approval;
    private int grn_pending;
}

