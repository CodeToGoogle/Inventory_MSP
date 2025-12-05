package com.msp.product_service.controller;

import com.msp.product_service.entity.Product;
import com.msp.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService svc;

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product p){ return ResponseEntity.ok(svc.create(p)); }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Integer id){ return ResponseEntity.ok(svc.findById(id)); }

    @GetMapping
    public ResponseEntity<List<Product>> list(){ return ResponseEntity.ok(svc.list(0,50)); }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> pending(){
        return ResponseEntity.ok(Map.of(
                "products_missing_unit", svc.pendingCount()
        ));
    }
}

