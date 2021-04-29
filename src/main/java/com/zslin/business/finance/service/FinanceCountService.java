package com.zslin.business.finance.service;

import com.zslin.business.finance.dao.IFinanceDetailDao;
import com.zslin.business.finance.dto.FinanceCountDto;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 财务统计
 * 两种方式统计
 * Created by zsl on 2019/1/8.
 */
@Service
@AdminAuth(name = "账务统计", psn = "财务管理", url = "/admin/financeCount", type = "1", orderNum = 3)
public class FinanceCountService {

    @Autowired
    private IFinanceDetailDao financeDetailDao;

    public JsonResult count(String params) {
        String year = null;
        try {
            year = JsonTools.getJsonParam(JsonTools.getJsonParam(params, "conditions"), "year");
        } catch (Exception e) {
        }
        List<FinanceCountDto> in1;
        List<FinanceCountDto> out1;
        List<FinanceCountDto> in2;
        List<FinanceCountDto> out2;
        if(year==null || "".equals(year)) { //没有选择年份
            in1 = financeDetailDao.findCountAllGroupByMonth("1"); //按月入账
            out1 = financeDetailDao.findCountAllGroupByMonth("-1"); //按月出账

            in2 = financeDetailDao.findCountAllGroupByCate("1"); //按分类入账
            out2 = financeDetailDao.findCountAllGroupByCate("-1"); //按分类出账
        } else {
            in1 = financeDetailDao.findCountGroupByMonth(year, "1"); //按月入账
            out1 = financeDetailDao.findCountGroupByMonth(year, "-1"); //按月出账

            in2 = financeDetailDao.findCountAllGroupByCate("1"); //按分类入账
            out2 = financeDetailDao.findCountAllGroupByCate("-1"); //按分类出账
        }
        Float totalIn = financeDetailDao.sum("1");
        Float totalOut = financeDetailDao.sum("-1");

        return JsonResult.success().set("totalIn", totalIn).set("totalOut", totalOut).
                set("in1", in1).set("in2", in2).set("out1", out1).set("out2", out2);
    }
}
