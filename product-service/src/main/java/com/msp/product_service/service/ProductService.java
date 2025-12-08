package com.msp.product_service.service;

import com.msp.product_service.dto.ProductRequest;
import com.msp.product_service.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest req);
    ProductResponse update(Integer id, ProductRequest req);
    ProductResponse get(Integer id);
    List<ProductResponse> list(int page, int size, String search);
    int pendingCount();
}


