package com.msp.product_service.controller;


import com.msp.product_service.dto.UnitDto;
import com.msp.product_service.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products/units")
@RequiredArgsConstructor
public class UnitController {
    private final UnitService svc;

    @PostMapping
    public ResponseEntity<UnitDto> create(@RequestBody UnitDto dto) {
        return ResponseEntity.ok(svc.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnitDto> update(@PathVariable Integer id, @RequestBody UnitDto dto) {
        return ResponseEntity.ok(svc.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitDto> get(@PathVariable Integer id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @GetMapping
    public ResponseEntity<List<UnitDto>> list() {
        return ResponseEntity.ok(svc.list());
    }
}
