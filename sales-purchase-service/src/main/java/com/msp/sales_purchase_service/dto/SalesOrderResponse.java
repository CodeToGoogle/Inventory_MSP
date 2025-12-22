package com.msp.sales_purchase_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalesOrderResponse {
    private Integer orderId;
    private String orderNumber;
    private LocalDate orderDate;
    private Integer customerId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private List<SalesOrderLineResponse> lines;

    @Data
    public static class SalesOrderLineResponse {
        private Integer lineId;
        private Integer productId;
        private BigDecimal orderedQty;
        private BigDecimal saleUnitPrice;
    }
}
