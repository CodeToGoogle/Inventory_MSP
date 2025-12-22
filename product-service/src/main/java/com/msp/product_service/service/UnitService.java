package com.msp.product_service.service;

import com.msp.product_service.dto.UnitDto;

import java.util.List;

public interface UnitService {
    UnitDto create(UnitDto dto);
    UnitDto update(Integer id, UnitDto dto);
    UnitDto get(Integer id);
    List<UnitDto> list();
}
