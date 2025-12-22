package com.msp.inventory_manufacturing_service.event;

import com.msp.inventory_manufacturing_service.entity.InventoryTransactions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final String topic = "inventory-events";

    @Value("${app.kafka.enabled:true}")
    private boolean kafkaEnabled;

    public void publishInventoryChanged(InventoryTransactions trx) {
        if (!kafkaEnabled) {
            log.info("Kafka disabled, skipping event: inventory.stock.changed");
            return;
        }
        Map<String,Object> evt = Map.of(
                "eventType","inventory.stock.changed",
                "transactionId", trx.getTransactionID(),
                "referenceType", trx.getReferenceType(),
                "referenceId", trx.getReferenceNumber(),
                "totalQty", trx.getTotalQty()
        );
        kafkaTemplate.send(topic, evt);
        log.info("Published inventory event: {}", evt);
    }

    public void publishMOEvent(Integer moId, String type) {
        if (!kafkaEnabled) {
            log.info("Kafka disabled, skipping event: manufacturing.{}", type);
            return;
        }
        Map<String,Object> evt = Map.of("eventType", "manufacturing."+type, "moId", moId);
        kafkaTemplate.send(topic, evt);
    }

    public void publishWOEvent(Integer woId, String type) {
        if (!kafkaEnabled) {
            log.info("Kafka disabled, skipping event: workorder.{}", type);
            return;
        }
        Map<String,Object> evt = Map.of("eventType", "workorder."+type, "woId", woId);
        kafkaTemplate.send(topic, evt);
    }
}
