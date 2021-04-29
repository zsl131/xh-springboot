package com.zslin.business.app.dto.orders;

import lombok.Data;

/**
 * 订单提成DTO对象
 */
@Data
public class OrdersRateDto {

    /** 当前代理的提成标准 */
    private Float thisAmount;

    /** 上级代理的提成标准 */
    private Float leaderAmount;
}
