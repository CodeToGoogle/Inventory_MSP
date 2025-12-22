package com.msp.sales_purchase_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalesEventProducer {
    private static final String TOPIC = "sales-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String eventType, Object payload, String key) {
        Map<String, Object> evt = Map.of(
                "eventType", eventType,
                "timestamp", Instant.now().toString(),
                "payload", payload
        );

        CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, key, evt);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Published event '{}' with key='{}' to topic '{}'", eventType, key, TOPIC);
            } else {
                log.error("Failed to publish event '{}' with key='{}' to topic '{}'", eventType, key, TOPIC, ex);
            }
        });
    }
}
