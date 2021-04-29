package com.zslin.business.mini.tools;

import com.zslin.business.dao.ICustomCommissionRecordDao;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.business.mini.dto.AgentCommissionDto;
import com.zslin.business.mini.dto.OrdersCountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序订单统计工具
 */
@Component
public class MiniOrdersTools {

    @Autowired
    private IOrdersDao ordersDao;

    @Autowired
    private ICustomCommissionRecordDao customCommissionRecordDao;

    /**
     * 生成用户提成DTO对象
     * @param agentId 代理ID
     * @return
     */
    public List<AgentCommissionDto> buildAgentCommission(Integer agentId) {
        List<AgentCommissionDto> result = new ArrayList<>();

        //agentId=null, money=0.0, totalCount=0, status=null
        //0-用户下单；1-用户付款，但不在提现期；2-在提现期；3-纳入结算清单；4-结算到账；5-结算失败
        AgentCommissionDto dto0 = customCommissionRecordDao.queryCountDto("0", agentId);
        dto0.setStatus("0"); dto0.setName("未付款");
        result.add(dto0);

        AgentCommissionDto dto1 = customCommissionRecordDao.queryCountDtoNoBatchNo("1", agentId);
        dto1.setStatus("1"); dto1.setName("已付款");
        result.add(dto1);

        AgentCommissionDto dto2 = customCommissionRecordDao.queryCountDtoNoBatchNo("2", agentId);
        dto2.setStatus("2"); dto2.setName("可提现");
        result.add(dto2);

        AgentCommissionDto dto3 = customCommissionRecordDao.queryCountDto("3", agentId);
        dto3.setStatus("3"); dto3.setName("结算清单");
        result.add(dto3);

        AgentCommissionDto dto4 = customCommissionRecordDao.queryCountDto("4", agentId);
        dto4.setStatus("4"); dto4.setName("已到账");
        result.add(dto4);

        return result;
    }

    /**
     * 生成用户的订单DTO对象
     * @param customId 顾客ID
     * @return
     */
    public List<OrdersCountDto> buildDto(Integer customId) {
        List<OrdersCountDto> result = new ArrayList<>();
        Integer count0 = ordersDao.queryCount("0", customId);
        result.add(new OrdersCountDto("0", "待付款", count0+""));

        Integer count1 = ordersDao.queryCount("1", customId);
        result.add(new OrdersCountDto("1", "待发货", count1+""));

        Integer count2 = ordersDao.queryCount("2", customId);
        result.add(new OrdersCountDto("2", "待收货", count2+""));

        Integer count3 = ordersDao.queryCount("3", customId);
        result.add(new OrdersCountDto("3", "待评价", count3+""));
        return result;
    }
}
