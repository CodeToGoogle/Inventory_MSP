package com.msp.product_service.service;

import com.msp.product_service.dto.CategoryDto;
import com.msp.product_service.entity.ProductCategory;
import com.msp.product_service.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final ProductCategoryRepository repo;

    @Override
    public CategoryDto create(CategoryDto dto) {
        ProductCategory p = ProductCategory.builder()
                .categoryName(dto.getCategoryName())
                .parentCategoryID(dto.getParentCategoryID())
                .description(dto.getDescription())
                .build();
        ProductCategory saved = repo.save(p);
        dto.setCategoryID(saved.getCategoryID());
        return dto;
    }

    @Override
    public CategoryDto update(Integer id, CategoryDto dto) {
        ProductCategory p = repo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        p.setCategoryName(dto.getCategoryName());
        p.setParentCategoryID(dto.getParentCategoryID());
        p.setDescription(dto.getDescription());
        repo.save(p);
        dto.setCategoryID(p.getCategoryID());
        return dto;
    }

    @Override
    public CategoryDto get(Integer id) {
        return repo.findById(id).map(c -> new CategoryDto(c.getCategoryID(), c.getCategoryName(), c.getParentCategoryID(), c.getDescription()))
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<CategoryDto> list() {
        return repo.findAll().stream()
                .map(c -> new CategoryDto(c.getCategoryID(), c.getCategoryName(), c.getParentCategoryID(), c.getDescription()))
                .collect(Collectors.toList());
    }
}

