package com.msp.product_service.service;

import com.msp.product_service.entity.ProductMaster;
import com.msp.product_service.exception.DuplicateSkuException;
import com.msp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ProductMaster createProduct(ProductMaster product) {
        log.info("Creating product. Received: {}", product);

        // Auto-generate SKU if not provided to avoid "SKU null" conflict
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            String newSku = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            product.setSku(newSku);
            log.info("Auto-generated SKU: {}", newSku);
        }

        // Check if SKU already exists before saving (for better debugging)
        Optional<ProductMaster> existing = productRepository.findBySku(product.getSku());
        if (existing.isPresent()) {
            log.error("SKU {} already exists in database! Existing product ID: {}", product.getSku(), existing.get().getProductId());
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }

        try {
            ProductMaster savedProduct = productRepository.save(product);
            log.info("Product saved successfully: {}", savedProduct);
            kafkaTemplate.send("product.created", savedProduct);
            return savedProduct;
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to save product. SKU: {}", product.getSku(), e);
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }
    }

    @Override
    public ProductMaster updateProduct(Integer productId, ProductMaster productDetails) {
        ProductMaster product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + productId));

        product.setProductName(productDetails.getProductName());
        product.setSku(productDetails.getSku());
        // Description field removed from entity
        product.setProductCategoryId(productDetails.getProductCategoryId());
        product.setUnitId(productDetails.getUnitId());
        product.setCostPrice(productDetails.getCostPrice());
        product.setSellingPrice(productDetails.getSellingPrice());
        product.setReorderLevel(productDetails.getReorderLevel());
        product.setSafetyStock(productDetails.getSafetyStock());
        product.setIsActive(productDetails.getIsActive());

        try {
            ProductMaster updatedProduct = productRepository.save(product);
            kafkaTemplate.send("product.updated", updatedProduct);
            return updatedProduct;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateSkuException("Product with SKU " + productDetails.getSku() + " already exists");
        }
    }

    @Override
    public Optional<ProductMaster> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Page<ProductMaster> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
