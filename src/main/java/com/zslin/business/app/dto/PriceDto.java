package com.zslin.business.app.dto;

import lombok.Data;

/**
 * 价格DTO对象
 */
@Data
public class PriceDto {

    /** 最低售价 */
    private Float minPrice;

    /** 最高售价 */
    private Float maxPrice;

    /** 最低原价 */
    private Float minOriPrice;

    /** 最高原价 */
    private Float maxOriPrice;

    public PriceDto(Float minPrice, Float maxPrice, Float minOriPrice, Float maxOriPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minOriPrice = minOriPrice;
        this.maxOriPrice = maxOriPrice;
    }
}
