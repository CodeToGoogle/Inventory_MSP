package com.msp.sales_purchase_service.controller;

import com.msp.sales_purchase_service.entity.PurchaseOrder;
import com.msp.sales_purchase_service.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchase API", description = "APIs for managing purchase orders")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/orders")
    @Operation(summary = "Create a new purchase order")
    public PurchaseOrder createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        return purchaseService.createPurchaseOrder(purchaseOrder);
    }

    @PostMapping("/orders/{id}/receipt")
    @Operation(summary = "Create a purchase receipt (GRN)")
    public void createPurchaseReceipt(@PathVariable(value = "id") Integer orderId) {
        purchaseService.createPurchaseReceipt(orderId);
    }
}
