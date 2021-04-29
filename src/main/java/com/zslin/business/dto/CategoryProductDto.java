package com.zslin.business.dto;

import com.zslin.business.model.Product;
import com.zslin.business.model.ProductCategory;
import lombok.Data;

import java.util.List;

/**
 * 分类产品DTO对象
 */
@Data
public class CategoryProductDto {

    private ProductCategory category;

    private List<Product> proList;

    public CategoryProductDto(ProductCategory category, List<Product> proList) {
        this.category = category;
        this.proList = proList;
    }
}
