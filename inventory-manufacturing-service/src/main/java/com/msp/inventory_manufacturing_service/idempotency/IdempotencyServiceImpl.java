package com.msp.inventory_manufacturing_service.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRepository repo;

    @Override
    public Optional<IdempotencyKey> get(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        return repo.findByIdempotencyKey(key);
    }

    @Override
    public IdempotencyKey storeResponse(String key, String method, String path, String requestBody, String responseBody, int status) {
        IdempotencyKey rec = IdempotencyKey.builder()
                .idempotencyKey(key)
                .requestMethod(method)
                .requestPath(path)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .responseStatus(status)
                .build();
        return repo.save(rec);
    }
}
