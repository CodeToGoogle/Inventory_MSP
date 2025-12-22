package com.msp.sales_purchase_service.service;

import com.msp.sales_purchase_service.dto.SalesOrderResponse;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private static final String IDEMPOTENCY_CACHE = "idempotency";

    @Cacheable(
        value = IDEMPOTENCY_CACHE,
        key = "#idempotencyKey",
        unless = "#result == null"
    )
    public SalesOrderResponse getCachedResponse(String idempotencyKey) {
        return null; // Cache lookup only
    }

    @CachePut(value = IDEMPOTENCY_CACHE, key = "#idempotencyKey")
    public SalesOrderResponse cacheResponse(
            String idempotencyKey,
            SalesOrderResponse response) {
        return response;
    }
}
