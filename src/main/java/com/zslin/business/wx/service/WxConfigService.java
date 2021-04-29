package com.zslin.business.wx.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.wx.dao.IWxConfigDao;
import com.zslin.business.wx.model.WxConfig;
import com.zslin.business.wx.tools.WxConfigTools;
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
 * Created by 钟述林 on 2020-04-14.
 */
@Service
@AdminAuth(name = "微信配置管理", psn = "微信管理", orderNum = 2, type = "1", url = "/admin/wxConfig")
@Explain(name = "微信配置管理", notes = "微信配置管理")
public class WxConfigService {

    @Autowired
    private IWxConfigDao wxConfigDao;

    @Autowired
    private WxConfigTools wxConfigTools;

    @AdminAuth(name = "添加修改微信配置", orderNum = 2)
    @ExplainOperation(name = "添加修改微信配置", notes = "添加修改微信配置信息", params = {
            @ExplainParam(value = "id", name = "微信配置id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "保存成功的对象信息")
    })
    @Transactional
    public JsonResult addOrUpdate(String params) {
        WxConfig obj = JSONObject.toJavaObject(JSON.parseObject(params), WxConfig.class);
        ValidationDto vd = ValidationTools.buildValidate(obj);
        //System.out.println(vd);
        if(vd.isHasError()) { //如果有验证异常
            return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
        }

        WxConfig old = wxConfigDao.loadOne();
        if(old==null) {
            wxConfigDao.save(obj);
            wxConfigTools.setConfig(obj);
        } else {
            MyBeanUtils.copyProperties(obj, old);
            wxConfigDao.save(old);
            wxConfigTools.setConfig(old);
        }
        return JsonResult.succ(obj);
    }

    @ExplainOperation(name = "获取微信配置信息", notes = "通过ID获取对象", back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "获取到的对象信息")
    })
    @NeedAuth(need = false)
    public JsonResult loadOne(String params) {
        try {
//            Integer id = Integer.parseInt(JsonTools.getJsonParam(params, "id"));
//            MiniConfig obj = miniConfigDao.findOne(id);
            WxConfig obj = wxConfigDao.loadOne();
            return JsonResult.getInstance().set("obj", obj);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(e.getMessage());
        }
    }
}
