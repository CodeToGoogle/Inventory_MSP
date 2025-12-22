package com.msp.inventory_manufacturing_service.service;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.msp.inventory_manufacturing_service.dto.CreateMORequest;
import com.msp.inventory_manufacturing_service.entity.*;
import com.msp.inventory_manufacturing_service.event.InventoryEventProducer;
import com.msp.inventory_manufacturing_service.exception.BusinessException;
import com.msp.inventory_manufacturing_service.idempotency.IdempotencyService;
import com.msp.inventory_manufacturing_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManufacturingServiceImpl implements ManufacturingService {

    private final MORepository moRepo;
    private final MOLineRepository moLineRepo;
    private final BOMRepository bomRepo;
    private final BOMLineRepository bomLineRepo;
    private final WorkOrderRepository woRepo;
    private final InventoryService inventoryService;
    private final InventoryEventProducer eventProducer;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public Map<String,Object> createMO(CreateMORequest req, String idempotencyKey) {

        // idempotency check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = idempotencyService.get(idempotencyKey);
            if (existing.isPresent()) {
                try { return mapper.readValue(existing.get().getResponseBody(), Map.class); } catch (Exception e) {}
            }
        }

        // create MO
        ManufacturingOrder mo = ManufacturingOrder.builder()
                .orderNumber("MO-"+System.currentTimeMillis())
                .referenceSO(req.getReferenceSo())
                .orderDate(LocalDate.now())
                .productID(req.getProductId())
                .totalQty(req.getTotalQty())
                .unitID(req.getUnitId())
                .orderStatus("Planned")
                .deliveryStatus("Pending")
                .build();

        // find BOM for product (choose active BOM)
        List<BillOfMaterials> boms = bomRepo.findByProductIdAndIsActive(req.getProductId(), true);
        if (boms.isEmpty()) {
            throw new BusinessException("BOM_NOT_FOUND","No BOM found for product "+req.getProductId(), HttpStatus.BAD_REQUEST);
        }
        BillOfMaterials bom = boms.get(0);
        List<BillOfMaterialsLine> bomLines = bomLineRepo.findByBillOfMaterials_BomID(bom.getBomID());

        // Save MO and lines
        mo = moRepo.save(mo);

        List<ManufacturingOrderLine> moLines = new ArrayList<>();
        for (BillOfMaterialsLine bl : bomLines) {
            // qty for MO = component qty * MO.totalQty
            BigDecimal compQty = bl.getQty().multiply(req.getTotalQty());
            ManufacturingOrderLine mol = ManufacturingOrderLine.builder()
                    .manufacturingOrder(mo)
                    .productID(bl.getProductID())
                    .orderedQty(compQty)
                    .unitID(bl.getUnitID())
                    .deliveredQty(BigDecimal.ZERO)
                    .build();
            moLines.add(mol);
        }
        mo.setLines(moLines);
        mo = moRepo.save(mo);

        // Now create Work Orders per operation (simple pattern: one WO per BOM line)
        List<WorkOrder> wos = new ArrayList<>();
        for (ManufacturingOrderLine mol : mo.getLines()) {
            WorkOrder wo = WorkOrder.builder()
                    .workOrderNumber("WO-"+System.currentTimeMillis()+"-"+new Random().nextInt(9999))
                    .referenceMO(mo.getOrderID())
                    .workcenterID(null)
                    .operationID(null)
                    .productID(mol.getProductID())
                    .orderedQty(mol.getOrderedQty())
                    .unitID(mol.getUnitID())
                    .wcStatus("Pending")
                    .build();
            wos.add(wo);
        }
        woRepo.saveAll(wos);

        // Publish event
        eventProducer.publishMOEvent(mo.getOrderID(), "created");

        Map<String,Object> resp = Map.of("moId", mo.getOrderID(), "moNumber", mo.getOrderNumber());
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try { idempotencyService.storeResponse(idempotencyKey,"POST","/manufacturing/mo",mapper.writeValueAsString(req),mapper.writeValueAsString(resp),HttpStatus.OK.value()); } catch (Exception ignored) {}
        }
        return resp;
    }
}
