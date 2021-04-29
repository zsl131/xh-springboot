package com.zslin.business.app.dto.orders;

import com.zslin.business.model.Product;
import com.zslin.business.model.ProductSpecs;
import lombok.Data;

/**
 * 订单处理的产品DTO对象
 */
@Data
public class OrdersProductDto {

    private Product product;

    private ProductSpecs specs;

    private Integer amount;

    public OrdersProductDto(Product product, ProductSpecs specs, Integer amount) {
        this.product = product;
        this.specs = specs;
        this.amount = amount;
    }
}
