package com.zslin.business.timer;

import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.model.Orders;
import com.zslin.business.tools.SendTemplateMessageTools;
import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.tools.TemplateMessageTools;
import com.zslin.core.common.NormalTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单定时功能
 */
@Component("ordersTimer")
@HasTemplateMessage
public class OrdersTimer {

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private SendTemplateMessageTools sendTemplateMessageTools;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    /**
     * 自动确认收货长时间未确认的订单
     */
    public void confirmOrders() {
        Long timeout = System.currentTimeMillis() - 7 * 24 * 3600 * 1000; //当前时间减去7天
        List<Orders> allList = ordersDao.findTimeoutConfirmOrders(timeout);
        for(Orders orders : allList) {
            handleTimeoutConfirmOrders(orders);
        }
    }

    //自动确认收货
    @Transactional
    @TemplateMessageAnnotation(name = "订单确认收货通知", keys = "订单编号-订单金额-确认时间")
    private void handleTimeoutConfirmOrders(Orders orders) {
        String ordersNo = orders.getOrdersNo();
        String curTime = NormalTools.curDatetime();
        orders.setStatus("3");
        orders.setEndDay(NormalTools.curDate());
        orders.setEndTime(curTime);
        orders.setEndLong(System.currentTimeMillis());

        ordersDao.save(orders);

        customCommissionRecordDao.updateStatusNoBatchNo("2", ordersNo); //修改提成记录状态

        sendTemplateMessageTools.send(orders.getOpenid(), "订单确认收货通知", "", "您的订单自动确认收货啦",
                TemplateMessageTools.field("订单编号", ordersNo),
                TemplateMessageTools.field("订单金额", orders.getTotalMoney()+" 元"),
                TemplateMessageTools.field("确认时间", curTime),

                TemplateMessageTools.field("超7未确认的订单自动确认收货"));
    }

    /**
     * 关闭长时间未付款的订单
     */
    public void closeTimeoutOrders() {
        Long timeout = System.currentTimeMillis() - 48 * 3600 * 1000; //当前时间减去48小时
        List<Orders> allList = ordersDao.findTimeoutOrders(timeout);
        for(Orders orders : allList) {
            handleTimeoutOrders(orders);
        }
    }

    //关闭订单分三步：1、修改订单状态；2、修改提成记录状态；3、通知顾客
    @Transactional
    @TemplateMessageAnnotation(name = "订单关闭提醒", keys = "订单商品-订单编号-下单时间-订单金额-关闭时间")
    private void handleTimeoutOrders(Orders orders) {
        String ordersNo = orders.getOrdersNo();
        String curTime = NormalTools.curDatetime();
        orders.setStatus("-1");
        orders.setEndLong(System.currentTimeMillis());
        orders.setEndTime(curTime);
        orders.setEndDay(NormalTools.curDate());
        ordersDao.save(orders); //修改订单状态
//        ordersDao.updateStatus("-1", orders.getOrdersNo(), orders.getCustomId()); //修改订单状态
        customCommissionRecordDao.updateStatus("-1", ordersNo); //修改提成记录状态

        sendTemplateMessageTools.send(orders.getOpenid(), "订单关闭提醒", "", "您的订单被关闭",
                TemplateMessageTools.field("订单商品", "满山晴-优质商品"),
                TemplateMessageTools.field("订单编号", ordersNo),
                TemplateMessageTools.field("下单时间", orders.getCreateTime()),
                TemplateMessageTools.field("订单金额", orders.getTotalMoney()+" 元"),
                TemplateMessageTools.field("关闭时间", curTime),

                TemplateMessageTools.field("超48小时未付款订单自动关闭"));
    }
}
