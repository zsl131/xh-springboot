package com.zslin.business.dto;

import com.zslin.business.model.ProductCategory;
import lombok.Data;

import java.util.List;

@Data
public class CategorySelectDto {

    /*private ProductCategory category;

    private List<ProductCategory> children;

    public CategorySelectDto(ProductCategory category, List<ProductCategory> children) {
        this.category = category;
        this.children = children;
    }*/

    private String label;

    private Integer value;

    private List<CategorySelectDto> children;

    public CategorySelectDto(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public CategorySelectDto(String label, Integer value, List<CategorySelectDto> children) {
        this.label = label;
        this.value = value;
        this.children = children;
    }
}
