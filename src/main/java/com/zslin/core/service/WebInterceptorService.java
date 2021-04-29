package com.zslin.core.service;

import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dao.IBaseAppConfigDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.model.BaseAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zsl on 2018/7/24.
 */
@Service(value = "webInterceptorService")
@Explain(name = "配置管理", notes = "获取所有配置信息，如果有微信配置这些也可以通过这里获取")
public class WebInterceptorService {

    @Autowired
    private IBaseAppConfigDao appConfigDao;

    @NeedAuth(need = false)
    @ExplainOperation(name = "获取配置", notes = "获取系统配置", back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "系统配置对象")
    })
    public JsonResult loadWebBase(String params) {
        BaseAppConfig ac = appConfigDao.loadOne();
        return JsonResult.succ(ac);
    }
}
