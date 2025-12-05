package com.msp.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditProducer {
    private static final String TOPIC = "auth-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(Integer userId, String userName) {
        var evt = Map.of(
                "eventType", "auth.user.created",
                "entityId", "USER-"+userId,
                "timestamp", Instant.now().toString(),
                "payload", Map.of("userId", userId, "username", userName)
        );
        kafkaTemplate.send(TOPIC, "user-"+userId, evt)
                .addCallback(s -> log.info("PUBLISHED user.created userId={}", userId),
                        f -> log.error("FAILED_PUBLISH user.created userId={}", userId, f));
    }
}
