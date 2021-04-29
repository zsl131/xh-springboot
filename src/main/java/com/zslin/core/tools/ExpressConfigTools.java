package com.zslin.core.tools;

import com.zslin.business.dao.IExpressConfigDao;
import com.zslin.business.model.ExpressConfig;
import com.zslin.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 物流配置工具类
 * Created by zsl on 2018/12/1.
 */
@Component
public class ExpressConfigTools {

    @Autowired
    private IExpressConfigDao expressConfigDao;

    private static ExpressConfig instance;

    public ExpressConfig getExpressConfig() throws BusinessException {
        if(instance==null) {
            instance = expressConfigDao.loadOne();
        }
        if(instance==null) {throw new BusinessException(BusinessException.Code.CONFIG_NULL, "未检测到物流配置信息");}
        return instance;
    }

    public void setConfig(ExpressConfig config) {
        instance = config;
    }
}
