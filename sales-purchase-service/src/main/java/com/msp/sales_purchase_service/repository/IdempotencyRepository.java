package com.msp.sales_purchase_service.repository;

import com.msp.sales_purchase_service.idempotency.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, Integer> {
    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);
}
