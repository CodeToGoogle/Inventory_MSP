package com.msp.inventory_manufacturing_service.service;

import com.msp.inventory_manufacturing_service.dto.CreateInventoryTransactionRequest;
import com.msp.inventory_manufacturing_service.dto.InventoryLevelsResponse;
import com.msp.inventory_manufacturing_service.dto.PendingSummaryDto;

import java.util.Map;

public interface InventoryService {
    Map<String,Object> createTransaction(CreateInventoryTransactionRequest req, String idempotencyKey);
    InventoryLevelsResponse getLevels(Integer productId);
    PendingSummaryDto getPendingSummary();
    void reserveStock(Integer productId, java.math.BigDecimal qty);
    void releaseReserved(Integer productId, java.math.BigDecimal qty);
}
