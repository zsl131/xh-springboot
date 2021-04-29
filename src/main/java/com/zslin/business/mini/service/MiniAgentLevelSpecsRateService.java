package com.zslin.business.mini.service;

import com.zslin.business.app.tools.RateTools;
import com.zslin.business.dao.IAgentDao;
import com.zslin.business.dao.IAgentLevelDao;
import com.zslin.business.dao.IProductDao;
import com.zslin.business.model.Agent;
import com.zslin.business.model.AgentLevel;
import com.zslin.business.model.Product;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.RateDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小程序端提成标准
 */
@Service
public class MiniAgentLevelSpecsRateService {

    @Autowired
    private IAgentLevelDao agentLevelDao;

    @Autowired
    private IProductDao productDao;

    @Autowired
    private RateTools rateTools;

    @Autowired
    private IAgentDao agentDao;

    @NeedAuth(openid = true)
    public JsonResult query(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        JsonResult result = JsonResult.getInstance();
        List<AgentLevel> levelList = agentLevelDao.findAll();

        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<Product> res = productDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("status", "eq", "1")),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

//        System.out.println(res.getContent());

        Integer levelId = JsonTools.getParamInteger(params, "levelId");
        if(levelId==null) { //如果没有传，则默认取自己的
            Agent agent = agentDao.findByOpenid(customDto.getOpenid());
            levelId = agent.getLevelId();
        }
        List<RateDto> rateList = rateTools.buildRates(res.getContent(), levelId);
        //System.out.println("----------------------");
        //System.out.println(rateList);

//        return JsonResult.success().set("rateList", rateList);
        result.set("levelList", levelList).set("rateList", rateList).set("levelId", levelId);
        return result;
    }
}
