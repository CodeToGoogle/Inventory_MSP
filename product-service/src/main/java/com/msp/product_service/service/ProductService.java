package com.msp.product_service.service;

import com.msp.product_service.entity.ProductMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    ProductMaster createProduct(ProductMaster product);
    ProductMaster updateProduct(Integer productId, ProductMaster productDetails);
    Optional<ProductMaster> getProductById(Integer productId);
    Page<ProductMaster> getAllProducts(Pageable pageable);
}
