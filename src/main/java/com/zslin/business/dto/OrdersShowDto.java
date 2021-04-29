package com.zslin.business.dto;

import com.zslin.business.model.CustomCommissionRecord;
import com.zslin.business.model.Orders;
import com.zslin.business.model.OrdersProduct;
import lombok.Data;

import java.util.List;

/**
 * 订单显示DTO对象
 */
@Data
public class OrdersShowDto {

    /** 订单信息 */
    private Orders orders;

    /** 产品信息 */
    private List<OrdersProduct> productList;

    /** 代理佣金 */
    private List<CustomCommissionRecord> commissionRecordList;

    public OrdersShowDto(Orders orders, List<OrdersProduct> productList, List<CustomCommissionRecord> commissionRecordList) {
        this.orders = orders;
        this.productList = productList;
        this.commissionRecordList = commissionRecordList;
    }
}
