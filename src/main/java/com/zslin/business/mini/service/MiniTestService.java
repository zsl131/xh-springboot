package com.zslin.business.mini.service;

import com.zslin.business.mini.tools.PayTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiniTestService {

    @Autowired
    private PayTools payTools;

    /**
     * 测试支付
     * @param params
     * @return
     */
    public JsonResult pay(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String ip = JsonTools.getIP(params);
        String ordersNo = JsonTools.getJsonParam(params, "ordersNo");
        payTools.unifiedOrder(customDto, ip, ordersNo);
        return JsonResult.success();
    }
}
