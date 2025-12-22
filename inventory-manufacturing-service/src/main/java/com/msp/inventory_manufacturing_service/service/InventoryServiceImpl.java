package com.msp.inventory_manufacturing_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.dto.CreateInventoryTransactionRequest;
import com.msp.inventory_manufacturing_service.dto.InventoryLevelsResponse;
import com.msp.inventory_manufacturing_service.dto.PendingSummaryDto;
import com.msp.inventory_manufacturing_service.entity.InventoryTransactions;
import com.msp.inventory_manufacturing_service.entity.InventoryTransactionsLine;
import com.msp.inventory_manufacturing_service.entity.ProductMaster;
import com.msp.inventory_manufacturing_service.event.InventoryEventProducer;
import com.msp.inventory_manufacturing_service.exception.BusinessException;
import com.msp.inventory_manufacturing_service.idempotency.IdempotencyKey;
import com.msp.inventory_manufacturing_service.idempotency.IdempotencyService;
import com.msp.inventory_manufacturing_service.repository.BOMLineRepository;
import com.msp.inventory_manufacturing_service.repository.BillOfMaterialsRepository;
import com.msp.inventory_manufacturing_service.repository.InventoryTransactionRepository;
import com.msp.inventory_manufacturing_service.repository.ProductMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository trxRepo;
    private final ProductMasterRepository productRepo;
    private final InventoryEventProducer eventProducer;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final BillOfMaterialsRepository bomRepo; // will be created below
    private final BOMLineRepository bomLineRepo;

    @Override
    @Transactional
    public Map<String,Object> createTransaction(CreateInventoryTransactionRequest req, String idempotencyKey) {

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<IdempotencyKey> existing = idempotencyService.get(idempotencyKey);
            if (existing.isPresent()) {
                try {
                    return mapper.readValue(existing.get().getResponseBody(), Map.class);
                } catch (Exception e) { log.warn("failed to parse idempotent response", e); }
            }
        }

        // Map transactionType -> effect (increase/decrease)
        boolean isIncrease = switch (req.getTransactionType()) {
            case "Purchase Receipt", "Manufacturing Production" -> true;
            case "Sales Delivery", "Manufacturing Consumption" -> false;
            default -> throw new BusinessException("INVALID_TRX_TYPE", "Unknown transaction type", HttpStatus.BAD_REQUEST);
        };

        InventoryTransactions trx = InventoryTransactions.builder()
                .transactionNumber("TRX-"+System.currentTimeMillis())
                .transactionDate(LocalDateTime.now())
                .operationTypeID(resolveOpId(req.getTransactionType()))
                .referenceType(req.getReferenceType())
                .referenceNumber(req.getReferenceId())
                .fromLocationID(req.getFromLocationId())
                .toLocationID(req.getToLocationId())
                .totalQty(BigDecimal.ZERO)
                .build();

        trx = trxRepo.save(trx);

        BigDecimal totalQty = BigDecimal.ZERO;
        List<InventoryTransactionsLine> savedLines = new ArrayList<>();

        for (CreateInventoryTransactionRequest.Line l : req.getLines()) {
            InventoryTransactionsLine line = InventoryTransactionsLine.builder()
                    .transaction(trx)
                    .productID(l.getProductId())
                    .batchID(Integer.valueOf(l.getBatchId()))
                    .qty(l.getQty())
                    .unitID(l.getUnitId())
                    .build();
            savedLines.add(line);
            // update product QOH
            if (isIncrease) {
                increaseStock(l.getProductId(), l.getQty());
            } else {
                decreaseStock(l.getProductId(), l.getQty());
            }
            totalQty = totalQty.add(l.getQty());
        }
        trx.setTotalQty(totalQty);
        trx.setLines(savedLines);
        trxRepo.save(trx);

        Map<String,Object> resp = Map.of("transactionId", trx.getTransactionID(), "transactionNumber", trx.getTransactionNumber(), "totalQty", totalQty);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                idempotencyService.storeResponse(idempotencyKey,"POST","/inventory/transactions",mapper.writeValueAsString(req),mapper.writeValueAsString(resp),HttpStatus.OK.value());
            } catch (Exception e) { log.warn("failed to persist idempotency", e); }
        }

        eventProducer.publishInventoryChanged(trx);

        return resp;
    }

    private int resolveOpId(String type) {
        return switch (type) {
            case "Purchase Receipt" -> 1;
            case "Sales Delivery" -> 2;
            case "Manufacturing Consumption" -> 3;
            case "Manufacturing Production" -> 4;
            default -> 0;
        };
    }

    @Transactional
    void decreaseStock(Integer productId, BigDecimal qty) {
        // get product with pessimistic lock
        ProductMaster p = productRepo.findByIdForUpdate(productId).orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND","Product not found",HttpStatus.NOT_FOUND));
        BigDecimal available = (p.getQtyOnHand()==null?BigDecimal.ZERO:p.getQtyOnHand()).subtract(p.getReservedQty()==null?BigDecimal.ZERO:p.getReservedQty());
        if (available.compareTo(qty) < 0) throw new BusinessException("INSUFFICIENT_STOCK","Insufficient stock for product "+productId,HttpStatus.CONFLICT);
        p.setQtyOnHand(p.getQtyOnHand().subtract(qty));
        productRepo.save(p);
    }

    @Transactional
    void increaseStock(Integer productId, BigDecimal qty) {
        ProductMaster p = productRepo.findByIdForUpdate(productId).orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND","Product not found",HttpStatus.NOT_FOUND));
        p.setQtyOnHand((p.getQtyOnHand()==null?BigDecimal.ZERO:p.getQtyOnHand()).add(qty));
        productRepo.save(p);
    }

    @Override
    public InventoryLevelsResponse getLevels(Integer productId) {
        ProductMaster p = productRepo.findById(productId).orElse(null);
        if (p==null) return new InventoryLevelsResponse(productId, null, null, null);
        BigDecimal qoh = p.getQtyOnHand()==null?BigDecimal.ZERO:p.getQtyOnHand();
        BigDecimal reserved = p.getReservedQty()==null?BigDecimal.ZERO:p.getReservedQty();
        BigDecimal available = qoh.subtract(reserved);
        return new InventoryLevelsResponse(productId,qoh,available,reserved);
    }

    @Override
    public PendingSummaryDto getPendingSummary() {
        int low = productRepo.findLowStock().size();
        int mos = 0; // can compute from MORepository
        int wo = 0;
        return PendingSummaryDto.builder().low_stock_items(low).mo_pending_release(mos).work_orders_pending(wo).replenishment_required(0).build();
    }

    @Transactional
    public void reserveStock(Integer productId, BigDecimal qty) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) return;
        ProductMaster p = productRepo.findByIdForUpdate(productId).orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND","Product not found",HttpStatus.NOT_FOUND));
        BigDecimal qoh = p.getQtyOnHand()==null?BigDecimal.ZERO:p.getQtyOnHand();
        BigDecimal reserved = p.getReservedQty()==null?BigDecimal.ZERO:p.getReservedQty();
        BigDecimal available = qoh.subtract(reserved);
        if (available.compareTo(qty) < 0) throw new BusinessException("INSUFFICIENT_AVAIL_FOR_RESERVE","Not enough available to reserve",HttpStatus.CONFLICT);
        p.setReservedQty(reserved.add(qty));
        productRepo.save(p);
    }

    @Transactional
    public void releaseReserved(Integer productId, BigDecimal qty) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) return;
        ProductMaster p = productRepo.findByIdForUpdate(productId).orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND","Product not found",HttpStatus.NOT_FOUND));
        BigDecimal reserved = p.getReservedQty()==null?BigDecimal.ZERO:p.getReservedQty();
        p.setReservedQty(reserved.subtract(qty).max(BigDecimal.ZERO));
        productRepo.save(p);
    }
}
