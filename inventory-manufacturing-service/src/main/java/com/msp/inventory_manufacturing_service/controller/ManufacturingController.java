package com.msp.inventory_manufacturing_service.controller;

import com.msp.inventory_manufacturing_service.dto.CreateMORequest;
import com.msp.inventory_manufacturing_service.service.ManufacturingService;
import com.msp.inventory_manufacturing_service.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/manufacturing")
@RequiredArgsConstructor
@Tag(name = "Manufacturing API", description = "APIs for managing manufacturing and work orders")
public class ManufacturingController {

    private final ManufacturingService manufacturingService;
    private final WorkOrderService workOrderService;

    @PostMapping("/mo")
    @Operation(summary = "Create a new manufacturing order")
    public ResponseEntity<Map<String, Object>> createManufacturingOrder(@RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey,
                                                                        @RequestBody CreateMORequest req) {
        return ResponseEntity.ok(manufacturingService.createMO(req, idempotencyKey));
    }

    @PostMapping("/work-orders/{id}/start")
    @Operation(summary = "Start a work order")
    public ResponseEntity<Map<String, Object>> startWorkOrder(@PathVariable(value = "id") Integer woId,
                                                              @RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey) {
        return ResponseEntity.ok(workOrderService.startWorkOrder(woId, idempotencyKey));
    }

    @PostMapping("/work-orders/{id}/complete")
    @Operation(summary = "Complete a work order")
    public ResponseEntity<Map<String, Object>> completeWorkOrder(@PathVariable(value = "id") Integer woId,
                                                                 @RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey) {
        return ResponseEntity.ok(workOrderService.completeWorkOrder(woId, idempotencyKey));
    }
}
