package com.msp.inventory_manufacturing_service.controller;

import com.msp.inventory_manufacturing_service.dto.CreateInventoryTransactionRequest;
import com.msp.inventory_manufacturing_service.dto.InventoryLevelsResponse;
import com.msp.inventory_manufacturing_service.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "APIs for managing inventory")
public class InventoryController {
    private final InventoryService svc;

    @PostMapping("/transactions")
    @Operation(summary = "Create an inventory transaction")
    public ResponseEntity<Map<String,Object>> createTransaction(@RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey,
                                                                @RequestBody CreateInventoryTransactionRequest req) {
        return ResponseEntity.ok(svc.createTransaction(req, idempotencyKey));
    }

    @GetMapping("/levels")
    @Operation(summary = "Get inventory levels for a product")
    public ResponseEntity<InventoryLevelsResponse> getLevels(@RequestParam Integer productId){
        return ResponseEntity.ok(svc.getLevels(productId));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending inventory summary")
    public ResponseEntity<?> pending(){
        return ResponseEntity.ok(svc.getPendingSummary());
    }
}
