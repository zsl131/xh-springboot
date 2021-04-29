package com.zslin.business.app.tools;

import com.zslin.business.app.dto.orders.OrdersRateDto;
import com.zslin.business.dao.IAgentLevelSpecsRateDao;
import com.zslin.business.dao.IAgentRateDefaultDao;
import com.zslin.business.dao.IProductSpecsDao;
import com.zslin.business.model.AgentLevelSpecsRate;
import com.zslin.business.model.AgentRateDefault;
import com.zslin.business.model.Product;
import com.zslin.business.model.ProductSpecs;
import com.zslin.core.dto.RateDto;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 提成标准工具类
 */
@Component
public class RateTools {

    @Autowired
    private IAgentLevelSpecsRateDao agentLevelSpecsRateDao;

    @Autowired
    private IAgentRateDefaultDao agentRateDefaultDao;

    @Autowired
    private IProductSpecsDao productSpecsDao;

    /** 获取提成标准 */
    public OrdersRateDto getRate(Integer levelId, Integer specsId) {
        OrdersRateDto dto = new OrdersRateDto();
        //1.先获取具体的提成标准
        AgentLevelSpecsRate alsr = agentLevelSpecsRateDao.getRate(levelId, specsId);
        if(alsr!=null) {
            dto.setLeaderAmount(alsr.getLeaderAmount());
            dto.setThisAmount(alsr.getAmount());
        }
        if(dto.getLeaderAmount()==null || dto.getLeaderAmount()<0 || dto.getThisAmount()==null || dto.getThisAmount()<0) {
            //2.如果没有具体设置，则获取默认提成标准
            AgentRateDefault ard = agentRateDefaultDao.getRate(levelId);
            if(ard!=null) {
                if (dto.getLeaderAmount() == null || dto.getLeaderAmount()<0) {dto.setLeaderAmount(ard.getLeaderAmount()); }
                if (dto.getThisAmount() == null || dto.getThisAmount()<0) {dto.setThisAmount(ard.getAmount()); }
            }
        }
        //3.如果都没有设置，则默认为0
        if(dto.getLeaderAmount()==null || dto.getLeaderAmount()<0) {dto.setLeaderAmount(0f);}
        if(dto.getThisAmount()==null || dto.getThisAmount()<0) {dto.setThisAmount(0f);}
        return dto;
    }

    public List<RateDto> buildRates(List<Product> proList, Integer levelId) {
        List<RateDto> result = null;
        try {
            result = new ArrayList<>();
            for(Product pro:proList) {
                List<ProductSpecs> specsList = productSpecsDao.findByProId(pro.getId(), SimpleSortBuilder.generateSort("orderNo_a"));
               // System.out.println("=================================");
                //System.out.println(specsList);
    //            List<AgentLevelSpecsRate> rates = agentLevelSpecsRateDao.findByProduct(pro.getId());
                for(ProductSpecs spe : specsList) {
                    RateDto rd = new RateDto();
                    rd.setProId(pro.getId());
                    rd.setProTitle(pro.getTitle());
                    rd.setSpecsId(spe.getId());
                    rd.setSpecsName(spe.getName());
                    rd.setUnitName(pro.getUnits());
                    AgentLevelSpecsRate rate = agentLevelSpecsRateDao.getRate(levelId, spe.getId());
                    if(rate==null) {
                        AgentRateDefault def = agentRateDefaultDao.getRate(levelId);
                        rd.setRate(def.getAmount());
                    } else {
                        rd.setRate(rate.getAmount());
                    }
                    result.add(rd);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(result);
        return result;
    }
}
