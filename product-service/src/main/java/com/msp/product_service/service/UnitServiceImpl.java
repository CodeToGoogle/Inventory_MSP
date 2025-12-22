package com.msp.product_service.service;

import com.msp.product_service.dto.UnitDto;
import com.msp.product_service.entity.ProductUnit;
import com.msp.product_service.repository.ProductUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {
    private final ProductUnitRepository repo;

    @Override
    public UnitDto create(UnitDto dto) {
        ProductUnit u = ProductUnit.builder()
                .unitName(dto.getUnitName())
                .contains(dto.getContains())
                .referenceUnitID(dto.getReferenceUnitID())
                .conversionFactor(dto.getConversionFactor())
                .isBaseUnit(dto.getIsBaseUnit())
                .build();
        ProductUnit saved = repo.save(u);
        dto.setUnitID(saved.getUnitID());
        return dto;
    }

    @Override
    public UnitDto update(Integer id, UnitDto dto) {
        ProductUnit u = repo.findById(id).orElseThrow(() -> new RuntimeException("Unit not found"));
        u.setUnitName(dto.getUnitName());
        u.setContains(dto.getContains());
        u.setReferenceUnitID(dto.getReferenceUnitID());
        u.setConversionFactor(dto.getConversionFactor());
        u.setIsBaseUnit(dto.getIsBaseUnit());
        repo.save(u);
        dto.setUnitID(u.getUnitID());
        return dto;
    }

    @Override
    public UnitDto get(Integer id) {
        return repo.findById(id).map(u -> new UnitDto(u.getUnitID(), u.getUnitName(), u.getContains(), u.getReferenceUnitID(), u.getConversionFactor(), u.getIsBaseUnit()))
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    @Override
    public List<UnitDto> list() {
        return repo.findAll().stream()
                .map(u -> new UnitDto(u.getUnitID(), u.getUnitName(), u.getContains(), u.getReferenceUnitID(), u.getConversionFactor(), u.getIsBaseUnit()))
                .collect(Collectors.toList());
    }
}
