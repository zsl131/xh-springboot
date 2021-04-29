package com.zslin.business.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单提交的DTO对象
 */
@Data
public class SubmitOrdersDto implements Serializable {

    /** 订单的 */
    private String ordersKey;

    private Integer addressId;

    private Integer couponId;

    /** 对应代理ID */
    private Integer agentId;

    /** 对应的代理Openid */
    private String agentOpenid;

    private String remark;

    /**
     * _23-89-8_20-82-3_
     */
    private String productData;
}
