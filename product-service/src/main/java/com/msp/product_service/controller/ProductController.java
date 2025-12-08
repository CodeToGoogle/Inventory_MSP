package com.msp.product_service.controller;


import com.msp.product_service.dto.ProductRequest;
import com.msp.product_service.dto.ProductResponse;
import com.msp.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService svc;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody @Validated ProductRequest req) {
        return ResponseEntity.ok(svc.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Integer id, @RequestBody @Validated ProductRequest req) {
        return ResponseEntity.ok(svc.update(id, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Integer id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(svc.list(page, size, search));
    }

    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> pending() {
        int count = svc.pendingCount();
        return ResponseEntity.ok(Map.of(
                "products_missing_unit", count // simplified single metric; gateway expects JSON keys
        ));
    }
}


