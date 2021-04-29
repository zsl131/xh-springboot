package com.zslin.business.dto;

import com.zslin.business.model.ProductCategory;
import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeRootDto {

    private List<CategoryTreeDto> treeList;

    private List<ProductCategory> categoryList;

    public CategoryTreeRootDto() {
    }

    public CategoryTreeRootDto(List<CategoryTreeDto> treeList, List<ProductCategory> categoryList) {
        this.treeList = treeList;
        this.categoryList = categoryList;
    }
}
