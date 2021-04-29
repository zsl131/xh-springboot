package com.zslin.core.qiniu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.qiniu.dao.IQiniuConfigDao;
import com.zslin.core.qiniu.model.QiniuConfig;
import com.zslin.core.qiniu.tools.QiniuConfigTools;
import com.zslin.core.tools.MyBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zsl on 2018/12/1.
 */
@Service
@AdminAuth(name = "七牛配置管理", psn = "七牛管理", url = "/admin/qiniuConfig", type = "1", orderNum = 1)
public class QiniuConfigService {

    @Autowired
    private IQiniuConfigDao qiniuConfigDao;

    @Autowired
    private QiniuConfigTools qiniuConfigTools;

    public JsonResult loadOne(String params) {
        QiniuConfig wc = qiniuConfigDao.loadOne();
        if(wc==null) {wc = new QiniuConfig();}
        return JsonResult.succ(wc);
    }

    @Transactional
    public JsonResult save(String params) {
        QiniuConfig wc = JSONObject.toJavaObject(JSON.parseObject(params), QiniuConfig.class);
        QiniuConfig wcOld = qiniuConfigDao.loadOne();
        if(wcOld==null) {
            qiniuConfigDao.save(wc);
            qiniuConfigTools.setConfig(wc);
            return JsonResult.succ(wc);
        } else {
            MyBeanUtils.copyProperties(wc, wcOld);
            qiniuConfigDao.save(wcOld);
            qiniuConfigTools.setConfig(wcOld);
            return JsonResult.succ(wcOld);
        }
    }
}
