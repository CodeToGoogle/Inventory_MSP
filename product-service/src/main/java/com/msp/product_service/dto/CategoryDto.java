package com.msp.product_service.dto;


import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryDto {
    private Integer categoryID;

    @NotBlank
    private String categoryName;

    private Integer parentCategoryID;
    private String description;
}

