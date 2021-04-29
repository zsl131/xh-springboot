package com.zslin.business.app.tools;

import com.zslin.business.app.dto.OrdersCommissionDto;
import com.zslin.business.app.dto.orders.OrdersRateDto;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentLevelDao;
import com.zslin.business.dao.IOrdersCouponDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.ProductSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金管理
 */
@Component
public class CommissionTools {

    @Autowired
    private IAgentDao agentDao;

    @Autowired
    private IOrdersCouponDao ordersCouponDao;

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private RateTools rateTools;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    /**
     * 生成订单对应产品的佣金数据
     * @return
     */
    public List<OrdersCommissionDto> buildCommission(String openid, Integer ... specsIds) {
        Agent agent = agentDao.findOkByOpenid(openid);
        return buildCommission(agent, specsIds);
    }

    public List<OrdersCommissionDto> buildCommission(Agent agent, Integer ...specsIds) {
        List<Integer> tempSpeIds = new ArrayList<>();
        List<OrdersCommissionDto> result = new ArrayList<>();
        if(agent!=null && specsIds!=null) { //不为空，才能进行下一步操作
            for(Integer speId : specsIds) {
                if(!tempSpeIds.contains(speId)) {
                    OrdersRateDto rateDto = rateTools.getRate(agent.getLevelId(), speId); //佣金DTO对象
                    ProductSpecs specs = productSpecsDao.findOne(speId);
                    result.add(new OrdersCommissionDto(speId, specs.getName(), specs.getProId(),
                            specs.getProTitle(), rateDto.getThisAmount(), rateDto.getLeaderAmount()));
                    tempSpeIds.add(speId);
                }
            }
        }
        return result;
    }
}
