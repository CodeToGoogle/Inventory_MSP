package com.msp.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(Integer userId, String userName) {
        kafkaTemplate.send("user.created", Map.of("userId", userId, "userName", userName));
    }
}
