package com.zslin.business.mini.service;

import com.zslin.business.settlement.dao.ISaleRankingDao;
import com.zslin.business.settlement.model.Reward;
import com.zslin.business.settlement.model.SaleRanking;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.QueryListDto;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.repository.SimplePageBuilder;
import com.zslin.core.repository.SimpleSortBuilder;
import com.zslin.core.repository.SpecificationOperator;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.QueryTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/** 销售排名 */
@Service
public class MiniSaleRankingService {

    @Autowired
    private ISaleRankingDao saleRankingDao;

    /** 排名列表 */
    public JsonResult list(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String month = JsonTools.getJsonParam(params, "month");
        if(month==null || "".equals(month)) {
            month = NormalTools.getMonth("yyyyMM", -1); //获取上个月
        }
        QueryListDto qld = QueryTools.buildQueryListDto(params);
        Page<SaleRanking> res = saleRankingDao.findAll(QueryTools.getInstance().buildSearch(qld.getConditionDtoList(),
                new SpecificationOperator("belongMonth", "eq", month)),
                SimplePageBuilder.generate(qld.getPage(), qld.getSize(), SimpleSortBuilder.generateSort(qld.getSort())));

        SaleRanking ownRanking = saleRankingDao.findOne(month, customDto.getOpenid());

        return JsonResult.success().set("rankingList", res.getContent()).set("month", month).set("ownRanking", ownRanking);
    }
}
