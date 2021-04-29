package com.zslin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainParam;
import com.zslin.core.api.ExplainReturn;
import com.zslin.core.common.NormalTools;
import com.zslin.core.dao.IAdminUserDao;
import com.zslin.core.dao.IBaseAppConfigDao;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.model.AdminUser;
import com.zslin.core.model.BaseAppConfig;
import com.zslin.core.tools.InitSystemTools;
import com.zslin.core.tools.JsonTools;
import com.zslin.core.tools.MyBeanUtils;
import com.zslin.core.tools.SecurityUtil;
import com.zslin.core.validate.ValidationDto;
import com.zslin.core.validate.ValidationTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

/**
 * Created by zsl on 2018/7/8.
 */
@Service
@Explain(name = "系统配置管理", notes = "系统配置管理")
@AdminAuth(psn = "系统管理", name = "基础配置", orderNum = 1, type = "1", url = "/admin/basic/appConfig")
public class BaseAppConfigService {

    @Autowired
    private IBaseAppConfigDao baseAppConfigDao;

    @Autowired
    private InitSystemTools initSystemTools;

    @Autowired
    private IAdminUserDao userDao;

    @AdminAuth(name = "修改基础配置", orderNum = 2)
    @ExplainOperation(name = "修改基础配置", notes = "修改基础配置", params = {
            @ExplainParam(value = "id", name = "配置id", require = true, type = "int", example = "1"),
            @ExplainParam(value = "...", name = "其他信息", type = "Object", example = "对应其他数据")
    }, back = {
            @ExplainReturn(field = "obj", type = "Object", notes = "保存成功的对象信息")
    })
    @Transactional
    public JsonResult save(String params) {
        BaseAppConfig obj = JSONObject.toJavaObject(JSON.parseObject(params), BaseAppConfig.class);
        ValidationDto vd = ValidationTools.buildValidate(obj);
        //System.out.println(vd);
        if(vd.isHasError()) { //如果有验证异常
            return JsonResult.getInstance().failFlag(BusinessException.Code.VALIDATE_ERR, BusinessException.Message.VALIDATE_ERR, vd.getErrors());
        }

        BaseAppConfig old = baseAppConfigDao.loadOne();
        if(old==null) {
            baseAppConfigDao.save(obj);
        } else {
            MyBeanUtils.copyProperties(obj, old);
            baseAppConfigDao.save(old);
        }
        return JsonResult.succ(obj);
    }

    @NeedAuth(need = false)
    @ExplainOperation(name = "检测是否初始化系统", back = {
            @ExplainReturn(field = "message", type = "String", notes = "返回结果信息"),
            @ExplainReturn(field = "res", type="Boolean", notes = "true-已初始化;false-未初始化")
    })
    public JsonResult checkInit(String params) {
        BaseAppConfig ac = baseAppConfigDao.loadOne();
        if(ac!=null && "1".equals(ac.getInitFlag())) {
            return JsonResult.success("系统已初始化").set("res", true);
        } else {
            return JsonResult.success("系统未初始化").set("res", false);
        }
    }

    @NeedAuth(need = false)
    @ExplainOperation(name = "获取系统配置", back = {
            @ExplainReturn(field = "size", type = "int", notes = "1：已经存在，0：不存在"),
            @ExplainReturn(field = "datas", type = "Object", notes = "配置对象")
    })
    public JsonResult loadOne(String params) {
        BaseAppConfig ac = baseAppConfigDao.loadOne();
        return JsonResult.getInstance().set("size", ac==null?0:1).set("datas", ac);
    }

    @NeedAuth(need = false)
    @ExplainOperation(name = "初始化系统", params = {
            @ExplainParam(value = "appName", name = "项目名称", require = true, example = "项目名称"),
            @ExplainParam(value = "nickname", name = "管理员昵称", require = true, example = "系统管理员"),
            @ExplainParam(value = "username", name = "管理员用户名", require = true, example = "admin"),
            @ExplainParam(value = "password", name = "管理员密码", require = true, example = "123456")
    }, back = {
            @ExplainReturn(field = "message", notes = "初始化结果信息")
    })
    @Transactional
    public JsonResult initSystem(String params) throws BusinessException {
        BaseAppConfig ac = baseAppConfigDao.loadOne();
        if(ac!=null && "1".equals(ac.getInitFlag())) {
//            return JsonResult.getInstance().fail("系统已经初始化，不可重复操作");
            return JsonResult.getInstance().failFlag("系统已经初始化，不可重复操作");
        }
        try {
            ac = new BaseAppConfig();
            ac.setAppName(JsonTools.getJsonParam(params, "appName"));
//            ac.setCreateDate(NormalTools.curDatetime());
            ac.setInitFlag("1");

            AdminUser user = new AdminUser();
            user.setCreateDate(NormalTools.curDatetime());

            user.setNickname(JsonTools.getJsonParam(params, "nickname"));
            String username = JsonTools.getJsonParam(params, "username");
            String password = JsonTools.getJsonParam(params, "password");

            if(NormalTools.isNull(username)) {
                throw new BusinessException(BusinessException.Code.PARAM_NULL, "用户名[username]不能为空");
            }
            if(NormalTools.isNull(password)) {
                throw new BusinessException(BusinessException.Code.PARAM_NULL, "密码不能为空");
            }

            user.setPassword(SecurityUtil.md5(username, password));
            user.setStatus("1");
            user.setIsAdmin("1");
            user.setUsername(username);
            userDao.save(user);

            baseAppConfigDao.save(ac);
            initSystemTools.initSystem(user.getId());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(e.getMessage());
        }

        return JsonResult.getInstance().ok("系统初始化完成");
    }
}
