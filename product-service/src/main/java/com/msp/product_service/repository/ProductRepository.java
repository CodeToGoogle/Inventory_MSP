package com.msp.product_service.repository;

import com.msp.product_service.entity.ProductMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductMaster, Integer> {
}
