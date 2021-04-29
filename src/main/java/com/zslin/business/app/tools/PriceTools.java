package com.zslin.business.app.tools;

import com.zslin.business.app.dto.PriceDto;
import com.zslin.business.model.ProductSpecs;

import java.util.List;

/**
 * 处理价格工具类
 */
public class PriceTools {

    public static PriceDto buildPriceDto(List<ProductSpecs> specsList) {
        Float minPrice=0f, maxPrice=0f, minOriPrice=0f, maxOriPrice=0f;
        int index = 0;
        for(ProductSpecs specs:specsList) {
            if(index++<=0) {
                minPrice = maxPrice = specs.getPrice();
                minOriPrice = maxOriPrice = specs.getOriPrice();
            }
            if(specs.getPrice()>maxPrice) {maxPrice = specs.getPrice();}
            if(specs.getPrice()<minPrice) {minPrice = specs.getPrice();}
            if(specs.getOriPrice()>maxOriPrice) {maxOriPrice = specs.getOriPrice();}
            if(specs.getOriPrice()<minOriPrice) {minOriPrice = specs.getOriPrice();}
        }
        return new PriceDto(minPrice, maxPrice, minOriPrice, maxOriPrice);
    }
}
