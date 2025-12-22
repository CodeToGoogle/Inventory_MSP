package com.msp.inventory_manufacturing_service.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.dto.CreateInventoryTransactionRequest;
import com.msp.inventory_manufacturing_service.entity.ManufacturingOrder;
import com.msp.inventory_manufacturing_service.entity.WorkOrder;
import com.msp.inventory_manufacturing_service.event.InventoryEventProducer;
import com.msp.inventory_manufacturing_service.exception.BusinessException;
import com.msp.inventory_manufacturing_service.idempotency.IdempotencyService;
import com.msp.inventory_manufacturing_service.repository.MORepository;
import com.msp.inventory_manufacturing_service.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository woRepo;
    private final InventoryService inventoryService;
    private final MORepository moRepo;
    private final InventoryEventProducer eventProducer;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public Map<String,Object> startWorkOrder(Integer woId, String idempotencyKey) {

        String key = idempotencyKey == null ? null : idempotencyKey + "::start::" + woId;
        if (key != null) {
            var existing = idempotencyService.get(key);
            if (existing.isPresent()) {
                try {
                    return mapper.readValue(existing.get().getResponseBody(), Map.class);
                } catch (Exception e) {
                    log.warn("Failed to parse idempotent response", e);
                }
            }
        }

        WorkOrder wo = woRepo.findById(woId).orElseThrow(() -> new BusinessException("WO_NOT_FOUND","WO not found", HttpStatus.NOT_FOUND));
        if (!"Pending".equals(wo.getWcStatus())) throw new BusinessException("WO_INVALID_STATE","WO not in Pending", HttpStatus.BAD_REQUEST);

        // Reserve/consume raw materials: here the WO product is assumed to be component; we consume its qty from inventory
        // Use inventoryService.decreaseStock via createTransaction with transactionType = "Manufacturing Consumption"
        CreateInventoryTransactionRequest req = new CreateInventoryTransactionRequest();
        req.setTransactionType("Manufacturing Consumption");
        req.setReferenceType("WO");
        req.setReferenceId(wo.getWorkOrderID());
        CreateInventoryTransactionRequest.Line line = new CreateInventoryTransactionRequest.Line();
        line.setProductId(wo.getProductID());
        line.setQty(wo.getOrderedQty());
        line.setUnitId(wo.getUnitID());
        req.setLines(List.of(line));

        inventoryService.createTransaction(req, idempotencyKey);

        wo.setWcStatus("In Progress");
        wo.setActualStart(java.time.LocalDateTime.now());
        woRepo.save(wo);

        eventProducer.publishWOEvent(woId, "started");
        Map<String,Object> resp = Map.of("status","started","woId",woId);
        if (key != null) {
            try {
                idempotencyService.storeResponse(key, "POST", "/api/manufacturing/work-orders/" + woId + "/start", "", mapper.writeValueAsString(resp), HttpStatus.OK.value());
            } catch (Exception e) {
                log.warn("Failed to store idempotent response", e);
            }
        }
        return resp;
    }

    @Override
    @Transactional
    public Map<String,Object> completeWorkOrder(Integer woId, String idempotencyKey) {
        WorkOrder wo = woRepo.findById(woId).orElseThrow(() -> new BusinessException("WO_NOT_FOUND","WO not found", HttpStatus.NOT_FOUND));
        if (!"In Progress".equals(wo.getWcStatus())) throw new BusinessException("WO_INVALID_STATE","WO not in progress", HttpStatus.BAD_REQUEST);

        // Release reserved stock (since we consumed it at start, we just need to clear the reservation if it was reserved)
        // In this flow, we assume reservation happened before start.
        inventoryService.releaseReserved(wo.getProductID(), wo.getOrderedQty());
        
        // Note: We do NOT consume again here. Consumption happened at start.

        // Produce finished goods into inventory
        ManufacturingOrder mo = moRepo.findById(wo.getReferenceMO()).orElseThrow(() -> new BusinessException("MO_NOT_FOUND", "Manufacturing Order not found", HttpStatus.NOT_FOUND));
        CreateInventoryTransactionRequest productionReq = new CreateInventoryTransactionRequest();
        productionReq.setTransactionType("Manufacturing Production");
        productionReq.setReferenceType("MO");
        productionReq.setReferenceId(mo.getOrderID());
        CreateInventoryTransactionRequest.Line productionLine = new CreateInventoryTransactionRequest.Line();
        productionLine.setProductId(mo.getProductID());
        productionLine.setQty(mo.getTotalQty()); // Using MO total qty as per previous fix
        productionLine.setUnitId(mo.getUnitID());
        productionReq.setLines(List.of(productionLine));
        inventoryService.createTransaction(productionReq, idempotencyKey + "::production");

        wo.setWcStatus("Completed");
        wo.setActualEnd(java.time.LocalDateTime.now());
        woRepo.save(wo);

        // Update MO: if all WOs completed -> mark MO completed
        List<WorkOrder> wos = woRepo.findByReferenceMO(mo.getOrderID());
        boolean allDone = wos.stream().allMatch(w -> "Completed".equals(w.getWcStatus()));
        if (allDone) {
            mo.setOrderStatus("Completed");
            mo.setCompletionDate(java.time.LocalDate.now());
            moRepo.save(mo);
            eventProducer.publishMOEvent(mo.getOrderID(), "completed");
        }

        eventProducer.publishWOEvent(woId, "completed");
        return Map.of("status","completed","woId",woId);
    }
}
