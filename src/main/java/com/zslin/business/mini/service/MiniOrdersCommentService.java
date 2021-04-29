package com.zslin.business.mini.service;

import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.mini.dao.IOrdersCommentDao;
import com.zslin.business.mini.model.OrdersComment;
import com.zslin.business.model.Orders;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiniOrdersCommentService {

    @Autowired
    private IOrdersCommentDao ordersCommentDao;

    @Autowired
    private IOrdersDao ordersDao;

    /** 订单点评 */
    @NeedAuth(openid = true)
    public JsonResult comment(String params) {
        String content = JsonTools.getJsonParam(params, "content");
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        Orders orders = ordersDao.findByOrdersNoAndCustomId(ordersNo, customDto.getCustomId());
        if(orders!=null) {
            OrdersComment oc = new OrdersComment();
            oc.setContent(content);
            oc.setCreateDay(NormalTools.curDate());
            oc.setCreateLong(System.currentTimeMillis());
            oc.setCreateTime(NormalTools.curDatetime());
            oc.setCustomId(orders.getCustomId());
            oc.setCustomNickname(orders.getNickname());
            oc.setOpenid(orders.getOpenid());
            oc.setOrdersId(orders.getId());
            oc.setOrdersNo(ordersNo);
            oc.setHeadImgUrl(orders.getHeadImgUrl());
            oc.setStatus("0");
            ordersCommentDao.save(oc);

            //修改订单状态为：已完成
            ordersDao.updateStatus("4", ordersNo, customDto.getCustomId());
        }
        return JsonResult.success("点评成功");
    }
}
