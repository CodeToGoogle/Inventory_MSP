package com.msp.sales_purchase_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

@Component
public class InventoryClient {
    private final WebClient webClient;

    public InventoryClient(WebClient.Builder webClientBuilder, @Value("${inventory.base.url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<Map> getLevels(Integer productId) {
        return webClient.get()
                .uri("/inventory/levels?productId={id}", productId)
                .retrieve().bodyToMono(Map.class);
    }

    public Mono<Map> createTransaction(Map payload, String idempotencyKey) {
        return webClient.post().uri("/inventory/transactions")
                .header("Idempotency-Key", idempotencyKey == null ? "" : idempotencyKey)
                .bodyValue(payload)
                .retrieve().bodyToMono(Map.class);
    }
}
