package com.zslin.business.mini.dto;

import lombok.Data;

/**
 * 订单统计DTO
 */
@Data
public class OrdersCountDto {

    /** 类型，对应Orders中的status */
    private String type;

    /** 名称，方便显示 */
    private String name;

    /** 具体数值，使用String类型的原因：可以传任何值 */
    private String amount;

    public OrdersCountDto(String type, String name, String amount) {
        this.type = type;
        this.name = name;
        this.amount = amount;
    }
}
