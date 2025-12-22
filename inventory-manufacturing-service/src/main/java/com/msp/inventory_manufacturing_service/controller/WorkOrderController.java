package com.msp.inventory_manufacturing_service.controller;

import com.msp.inventory_manufacturing_service.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/manufacturing/wo")
@RequiredArgsConstructor
public class WorkOrderController {
    private final WorkOrderService svc;

    @PostMapping("/{id}/start")
    public ResponseEntity<Map<String,Object>> start(@PathVariable Integer id, @RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey){
        return ResponseEntity.ok(svc.startWorkOrder(id, idempotencyKey));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String,Object>> complete(@PathVariable Integer id, @RequestHeader(value="Idempotency-Key", required=false) String idempotencyKey){
        return ResponseEntity.ok(svc.completeWorkOrder(id, idempotencyKey));
    }
}

