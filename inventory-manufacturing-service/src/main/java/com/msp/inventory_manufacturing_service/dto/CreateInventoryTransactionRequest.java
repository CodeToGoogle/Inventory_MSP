package com.msp.inventory_manufacturing_service.dto;



import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateInventoryTransactionRequest {

    private String transactionType;   // e.g., "Sales Delivery", "Purchase Receipt"
    private String referenceType;     // SO, PO, MO, WO
    private Integer referenceId;
    private Integer fromLocationId;
    private Integer toLocationId;

    private List<Line> lines;

    @Data
    public static class Line {
        private Integer productId;
        private BigDecimal qty;
        private Integer unitId;
        private String batchId; // Corrected from Integer to String
    }
}
