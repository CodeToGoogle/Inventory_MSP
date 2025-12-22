package com.msp.sales_purchase_service.idempotency;

import java.util.Optional;

public interface IdempotencyService {
    Optional<IdempotencyKey> get(String key);
    IdempotencyKey storeResponse(String key, String method, String path, String requestBody, String responseBody, int status);
}
