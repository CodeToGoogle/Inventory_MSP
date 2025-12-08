package com.msp.product_service.repository;

import com.msp.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByProductNameContainingIgnoreCase(String q);

    @Query("SELECT count(p) FROM Product p WHERE p.unitID IS NULL")
    long countByUnitIDIsNull();

    @Query("SELECT count(p) FROM Product p WHERE p.productCategoryID IS NULL")
    long countByProductCategoryIDIsNull();

    @Query("SELECT count(p) FROM Product p WHERE p.qtyOnHand <= p.reorderLevel")
    long countByLowStock();

    @Query(value = "SELECT count(*) FROM product_master WHERE sku IS NULL OR sku = ''", nativeQuery = true)
    long countBySkuIsNullOrBlank();
}
