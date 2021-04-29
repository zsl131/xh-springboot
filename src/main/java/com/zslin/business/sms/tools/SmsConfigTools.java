package com.zslin.business.sms.tools;

import com.zslin.business.sms.dao.ISmsConfigDao;
import com.zslin.business.sms.model.SmsConfig;
import com.zslin.core.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zsl on 2018/9/25.
 */
@Component
public class SmsConfigTools {

    @Autowired
    private ISmsConfigDao smsConfigDao;

    private static SmsConfig instance;

    public SmsConfig getSmsConfig() throws SystemException {
        if(instance==null) {
            instance = smsConfigDao.loadOne();
        }
        if(instance==null) {throw new SystemException("未检测到短信配置信息");}
        return instance;
    }

    public void setSmsConfig(SmsConfig config) {
        instance = config;
    }
}
