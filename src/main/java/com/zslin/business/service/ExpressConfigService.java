package com.zslin.business.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.dao.IExpressConfigDao;
import com.zslin.business.model.ExpressConfig;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.tools.ExpressConfigTools;
import com.zslin.core.tools.MyBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2020-04-12.
 */
@Service
@AdminAuth(name = "物流接口配置管理", psn = "物流管理", orderNum = 2, type = "1", url = "/admin/expressConfig")
@Explain(name = "物流接口配置管理", notes = "物流接口配置管理")
public class ExpressConfigService {

    @Autowired
    private IExpressConfigDao expressConfigDao;

    @Autowired
    private ExpressConfigTools expressConfigTools;

    public JsonResult loadOne(String params) {
        ExpressConfig ec = expressConfigDao.loadOne();
        if(ec==null) {ec = new ExpressConfig();}
        return JsonResult.succ(ec);
    }

    @Transactional
    public JsonResult save(String params) {
        ExpressConfig ec = JSONObject.toJavaObject(JSON.parseObject(params), ExpressConfig.class);
        ExpressConfig wcOld = expressConfigDao.loadOne();
        if(wcOld==null) {
            expressConfigDao.save(ec);
            expressConfigTools.setConfig(ec);
            return JsonResult.succ(ec);
        } else {
            MyBeanUtils.copyProperties(ec, wcOld);
            expressConfigDao.save(wcOld);
            expressConfigTools.setConfig(wcOld);
            return JsonResult.succ(wcOld);
        }
    }


}
