package com.msp.sales_purchase_service.controller;

import com.msp.sales_purchase_service.dto.CreateSalesOrderRequest;
import com.msp.sales_purchase_service.dto.SalesOrderResponse;
import com.msp.sales_purchase_service.service.IdempotencyService;
import com.msp.sales_purchase_service.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales API", description = "APIs for managing sales orders")
public class SalesController {

    private final SalesService salesService;
    private final IdempotencyService idempotencyService;

    @PostMapping("/orders")
    @Operation(summary = "Create a new sales order")
    public ResponseEntity<SalesOrderResponse> createSalesOrder(
            @RequestBody CreateSalesOrderRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false)
            String idempotencyKey) {

        if (StringUtils.hasText(idempotencyKey)) {
            SalesOrderResponse cached =
                    idempotencyService.getCachedResponse(idempotencyKey);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }
        }

        SalesOrderResponse response = salesService.createSalesOrder(request, idempotencyKey);

        if (StringUtils.hasText(idempotencyKey)) {
            idempotencyService.cacheResponse(idempotencyKey, response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{id}/approve")
    @Operation(summary = "Approve a sales order")
    public SalesOrderResponse approveSalesOrder(@PathVariable(value = "id") Integer orderId) {
        return salesService.approveSalesOrder(orderId);
    }
}
