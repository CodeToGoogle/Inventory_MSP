package com.msp.product_service.service;

import com.msp.product_service.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto dto);
    CategoryDto update(Integer id, CategoryDto dto);
    CategoryDto get(Integer id);
    List<CategoryDto> list();
}

