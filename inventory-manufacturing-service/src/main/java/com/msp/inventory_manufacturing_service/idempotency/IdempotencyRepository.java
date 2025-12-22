package com.msp.inventory_manufacturing_service.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, Integer> {
    Optional<IdempotencyKey> findByIdempotencyKey(String key);
}
