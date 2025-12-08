package com.msp.product_service.service;

import com.msp.product_service.dto.ProductRequest;
import com.msp.product_service.dto.ProductResponse;
import com.msp.product_service.entity.Product;
import com.msp.product_service.event.ProductEventProducer;
import com.msp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final ProductEventProducer eventProducer;

    @Override
    public ProductResponse create(ProductRequest req) {
        Product p = Product.builder()
                .productName(req.getProductName())
                .productType(req.getProductType())
                .productCategoryID(req.getProductCategoryID())
                .unitID(req.getUnitID())
                .qtyOnHand(req.getQtyOnHand())
                .reorderLevel(req.getReorderLevel())
                .safetyStock(req.getSafetyStock())
                .costPrice(req.getCostPrice())
                .sellingPrice(req.getSellingPrice())
                .sku(req.getSku())
                .barcode(req.getBarcode())
                .isActive(true)
                .build();
        Product saved = repo.save(p);
        log.info("product.created productID={}", saved.getProductID());
        eventProducer.publishProductCreated(saved);
        return toResponse(saved);
    }

    @Override
    public ProductResponse update(Integer id, ProductRequest req) {
        Product p = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        p.setProductName(req.getProductName());
        p.setProductType(req.getProductType());
        p.setProductCategoryID(req.getProductCategoryID());
        p.setUnitID(req.getUnitID());
        p.setQtyOnHand(req.getQtyOnHand());
        p.setReorderLevel(req.getReorderLevel());
        p.setSafetyStock(req.getSafetyStock());
        p.setCostPrice(req.getCostPrice());
        p.setSellingPrice(req.getSellingPrice());
        p.setSku(req.getSku());
        p.setBarcode(req.getBarcode());
        Product saved = repo.save(p);
        eventProducer.publishProductUpdated(saved);
        return toResponse(saved);
    }

    @Override
    public ProductResponse get(Integer id) {
        return repo.findById(id).map(this::toResponse).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<ProductResponse> list(int page, int size, String search) {
        Pageable pg = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("productID").descending());
        Page<Product> p;
        if (search == null || search.isBlank()) {
            p = repo.findAll(pg);
        } else {
            p = new PageImpl<>(repo.findByProductNameContainingIgnoreCase(search)); // simple fallback; could implement paged search
        }
        return p.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public int pendingCount() {
        long missingUnit = repo.countByUnitIDIsNull();
        long missingCategory = repo.countByProductCategoryIDIsNull();
        long lowStock = repo.countByLowStock();
        long missingSku = repo.countBySkuIsNullOrBlank();
        return (int)(missingUnit + missingCategory + lowStock + missingSku);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .productID(p.getProductID())
                .productName(p.getProductName())
                .productType(p.getProductType())
                .productCategoryID(p.getProductCategoryID())
                .unitID(p.getUnitID())
                .qtyOnHand(p.getQtyOnHand())
                .reorderLevel(p.getReorderLevel())
                .safetyStock(p.getSafetyStock())
                .costPrice(p.getCostPrice())
                .sellingPrice(p.getSellingPrice())
                .sku(p.getSku())
                .barcode(p.getBarcode())
                .isActive(p.getIsActive())
                .build();
    }
}
