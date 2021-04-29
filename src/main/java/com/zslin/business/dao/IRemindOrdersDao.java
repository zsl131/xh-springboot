package com.zslin.business.dao;

import com.zslin.business.model.RemindOrders;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by 钟述林 on 2020-04-15.
 */
public interface IRemindOrdersDao extends BaseRepository<RemindOrders, Integer>, JpaSpecificationExecutor<RemindOrders> {

    RemindOrders findByOrdersNo(String ordersNo);

    @Query("FROM RemindOrders r WHERE r.ordersNo=?1 AND r.createLong>?2")
    RemindOrders findByOrdersNoAndTime(String ordersNo, Long createLong);
}
