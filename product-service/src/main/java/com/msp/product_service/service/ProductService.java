package com.msp.product_service.service;

import com.msp.product_service.entity.Product;

import java.util.List;
public interface ProductService {
    Product create(Product p);
    Product update(Product p);
    Product findById(Integer id);
    List<Product> list(int page, int size);
    int pendingCount(); // for /pending
}

