package com.msp.product_service.repository;

import com.msp.product_service.entity.ProductMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductMaster, Integer> {
    Optional<ProductMaster> findBySku(String sku);
}
