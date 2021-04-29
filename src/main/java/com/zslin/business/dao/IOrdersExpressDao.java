package com.zslin.business.dao;

import com.zslin.business.model.OrdersExpress;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by 钟述林 on 2020-04-12.
 */
public interface IOrdersExpressDao extends BaseRepository<OrdersExpress, Integer>, JpaSpecificationExecutor<OrdersExpress> {

    List<OrdersExpress> findByOrdersNo(String ordersNo);

    List<OrdersExpress> findByOrdersId(Integer ordersId);

    //OrdersExpress findByOrdersId(Integer ordersId);

    /** 获取产品物流信息 */
    List<OrdersExpress> findByOrdersProId(Integer ordersProId);

    List<OrdersExpress> findByExpNo(String expNo);
}
