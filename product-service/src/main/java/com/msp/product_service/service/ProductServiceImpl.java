package com.msp.product_service.service;

import com.msp.product_service.entity.ProductMaster;
import com.msp.product_service.exception.DuplicateSkuException;
import com.msp.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ProductMaster createProduct(ProductMaster product) {
        try {
            ProductMaster savedProduct = productRepository.save(product);
            kafkaTemplate.send("product.created", savedProduct);
            return savedProduct;
        } catch (DataIntegrityViolationException e) {
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
