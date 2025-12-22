package com.msp.inventory_manufacturing_service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msp.inventory_manufacturing_service.service.ManufacturingService;
import com.msp.inventory_manufacturing_service.dto.CreateMORequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesPurchaseConsumer {

    private final ManufacturingService manufacturingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "sales.approved", groupId = "inventory-manufacturing-service")
    public void handleSalesOrderApproved(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");
            Integer productId = (Integer) payload.get("productId");
            BigDecimal quantity = new BigDecimal(payload.get("quantity").toString());

            CreateMORequest createMORequest = new CreateMORequest();
            createMORequest.setProductId(productId);
            createMORequest.setTotalQty(quantity);
            createMORequest.setUnitId(1);
            createMORequest.setReferenceSo((Integer) payload.get("orderId"));

            manufacturingService.createMO(createMORequest, null);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing sales.approved event", e);
        }
    }

    @KafkaListener(topics = "purchase.grn.created", groupId = "inventory-manufacturing-service")
    public void handlePurchaseGrnCreated(String message) {
        log.info("Received purchase.grn.created event: " + message);
    }
}
