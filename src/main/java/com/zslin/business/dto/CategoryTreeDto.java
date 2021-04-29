package com.zslin.business.dto;

import com.zslin.business.model.ProductCategory;
import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeDto {

    private ProductCategory category;

    private List<CategoryProductDto> children;

    public CategoryTreeDto(ProductCategory category, List<CategoryProductDto> children) {
        this.category = category;
        this.children = children;
    }
}
