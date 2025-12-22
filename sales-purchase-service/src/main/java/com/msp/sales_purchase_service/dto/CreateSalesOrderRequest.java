package com.msp.sales_purchase_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSalesOrderRequest {
    private Integer customerId;
    private List<SalesOrderLineRequest> lines;

    @Data
    public static class SalesOrderLineRequest {
        private Integer productId;
        private BigDecimal orderedQty;
        private Integer unitId;
        private BigDecimal saleUnitPrice;
    }
}
