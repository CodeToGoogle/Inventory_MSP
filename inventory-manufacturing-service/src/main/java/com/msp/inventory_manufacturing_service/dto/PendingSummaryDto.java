package com.msp.inventory_manufacturing_service.dto;


import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PendingSummaryDto {
    private int low_stock_items;
    private int mo_pending_release;
    private int work_orders_pending;
    private int replenishment_required;
}
