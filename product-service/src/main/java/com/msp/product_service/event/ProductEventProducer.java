package com.msp.product_service.event;

import com.msp.product_service.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {
    private static final String TOPIC = "product-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(Product p) {
        var evt = Map.of(
                "eventType","product.created",
                "entityId","PRODUCT-"+p.getProductID(),
                "timestamp", Instant.now().toString(),
                "payload", Map.of("productId", p.getProductID(), "name", p.getProductName())
        );
        
        kafkaTemplate.send(TOPIC, "product-" + p.getProductID(), evt).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("published product.created id={}", p.getProductID());
            } else {
                log.error("failed to publish product.created id={}", p.getProductID(), ex);
            }
        });
    }

    public void publishProductUpdated(Product p) {
        var evt = Map.of(
                "eventType","product.updated",
                "entityId","PRODUCT-"+p.getProductID(),
                "timestamp", Instant.now().toString(),
                "payload", Map.of("productId", p.getProductID(), "name", p.getProductName())
        );
        
        kafkaTemplate.send(TOPIC, "product-" + p.getProductID(), evt);
    }
}
