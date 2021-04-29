package com.zslin.business.mini.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.dao.IMiniConfigDao;
import com.zslin.business.mini.model.MiniConfig;
import com.zslin.business.mini.tools.MiniConfigTools;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 钟述林 on 2019-12-01.
 */
@Service
@AdminAuth(name = "小程序配置管理", psn = "移动端管理", orderNum = 2, type = "1", url = "/admin/miniConfig")
@Explain(name = "小程序配置管理", notes = "小程序配置管理")
public class MiniConfigService {

    @Autowired
    private IMiniConfigDao miniConfigDao;

    @Autowired
    private MiniConfigTools miniConfigTools;

    @AdminAuth(name = "添加修改小程序配置", orderNum = 2)
    @ExplainOperation(name = "添加修改小程序配置", notes = "添加修改小程序配置信息", params = {
            @ExplainParam(value = "id", name = "小程序配置id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "保存成功的对象信息")
    })
    @Transactional
    public JsonResult addOrUpdate(String params) {
        MiniConfig obj = JSONObject.toJavaObject(JSON.parseObject(params), MiniConfig.class);
        ValidationDto vd = ValidationTools.buildValidate(obj);
        //System.out.println(vd);
        if(vd.isHasError()) { //如果有验证异常
            return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
        }

        MiniConfig old = miniConfigDao.loadOne();
        if(old==null) {
            miniConfigDao.save(obj);
            miniConfigTools.setConfig(obj);
        } else {
            MyBeanUtils.copyProperties(obj, old);
            miniConfigDao.save(old);
            miniConfigTools.setConfig(old);
        }
        return JsonResult.succ(obj);
    }

    @ExplainOperation(name = "获取小程序配置信息", notes = "通过ID获取角色对象", back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    @NeedAuth(need = false)
    public JsonResult loadOne(String params) {
        try {
//            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
//            MiniConfig obj = miniConfigDao.findOne(id);
            MiniConfig obj = miniConfigDao.loadOne();
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
