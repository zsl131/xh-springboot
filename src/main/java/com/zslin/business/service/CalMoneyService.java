package com.zslin.business.service;

import com.zslin.business.dao.ICashOutDao;
import com.zslin.business.dao.IOrdersDao;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalMoneyService {

    @Autowired
    private ICashOutDao cashOutDao;

    @Autowired
    private IOrdersDao ordersDao;

    /**
     * 统计
     * @param params
     * @return
     */
    public JsonResult onCal(String params) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        String startTime = JsonTools.getJsonParam(params, "startTime");
        String endTime = JsonTools.getJsonParam(params, "endTime");
        Long startLong = NormalTools.str2Long(startTime, pattern);
        Long endLong = NormalTools.str2Long(endTime, pattern);
        Double cashOutMoney = cashOutDao.findMoney(startLong, endLong); //已提现佣金金额

        return null;
    }
}
