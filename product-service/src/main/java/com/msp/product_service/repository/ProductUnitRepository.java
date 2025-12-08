package com.msp.product_service.repository;

import com.msp.product_service.entity.ProductUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Integer> {
}

