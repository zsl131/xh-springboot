package com.zslin.business.dao;

import com.zslin.business.model.Orders;
import com.zslin.core.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 钟述林 on 2019-12-18.
 */
public interface IOrdersDao extends BaseRepository<Orders, Integer>, JpaSpecificationExecutor<Orders> {

    Orders findByOrdersNo(String ordersNo);

    Orders findByOrdersKey(String ordersKey);

    @Query("FROM Orders o WHERE o.id=?1 AND o.customId=?2")
    Orders findOne(Integer id, Integer customId);

    Orders findByOrdersNoAndCustomId(String ordersNo, Integer customId);

    /** 查询订单编号，用于支付 */
    @Query("SELECT o.ordersNo FROM Orders o WHERE o.ordersKey=?1 AND o.customId=?2")
    String queryOrdersNo(String ordersKey, Integer customId);

    @Query("UPDATE Orders o SET o.status=?1 WHERE o.ordersNo=?2 AND o.customId=?3")
    @Modifying
    @Transactional
    int updateStatus(String status, String ordersNo, Integer customId);

    @Query("SELECT COUNT(o.id) FROM Orders o WHERE o.status=?1 AND o.customId=?2 ")
    Integer queryCount(String status, Integer customId);

    /** 获取超时未付款的订单 */
    @Query("FROM Orders o WHERE o.status='0' AND o.createLong<=?1")
    List<Orders> findTimeoutOrders(Long timeout);

    /** 获取长时间未确认收货的订单 */
    @Query("FROM Orders o WHERE o.status='2' AND o.sendLong<=?1")
    List<Orders> findTimeoutConfirmOrders(Long timeout);

    /** 获取时间段内订单金额总和 */
    @Query("SELECT SUM(o.totalMoney) FROM Orders o WHERE o.payLong IS NOT NULL AND o.payLong>=?1 AND o.payLong<=?2")
    Double findMoney(Long startLong, Long endLong);

    /** 获取时间段内订单优惠金额总和 */
    @Query("SELECT SUM(o.discountMoney) FROM Orders o WHERE o.discountMoney IS NOT NULL AND o.payLong IS NOT NULL AND o.payLong>=?1 AND o.payLong<=?2")
    Double findDiscountMoney(Long startLong, Long endLong);

    /** 获取时间段内订单退款 */
    /*@Query("SELECT SUM(o.backMoney) FROM Orders o WHERE o.backMoney IS NOT NULL  AND o.payLong IS NOT NULL AND o.payLong>=?1 AND o.payLong<=?2 ")
    Double findBackMoney(Long startLong, Long endLong);*/
}
