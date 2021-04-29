package com.zslin.business.mini.service;

import com.zslin.business.tools.BindCodeTools;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.dto.WxCustomDto;
import com.zslin.core.tools.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiniPublicService {

    @Autowired
    private BindCodeTools bindCodeTools;

    /** 获取验证码，用于绑定微信小程序 */
    @NeedAuth(openid = true)
    public JsonResult genBindCode(String params) {
        WxCustomDto customDto = JsonTools.getCustom(params);
        String code = bindCodeTools.getCode(customDto.getOpenid());
        return JsonResult.success("获取成功").set("code", code);
    }
}
