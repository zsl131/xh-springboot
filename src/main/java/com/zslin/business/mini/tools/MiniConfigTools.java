package com.zslin.business.mini.tools;

import com.zslin.business.mini.dao.IMiniConfigDao;
import com.zslin.business.mini.model.MiniConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MiniConfigTools {

    @Autowired
    private IMiniConfigDao miniConfigDao;

    private static MiniConfig instance;

    public MiniConfig getMiniConfig() {
        if(instance==null) {
            instance = miniConfigDao.loadOne();
        }
        return instance;
    }

    public void setConfig(MiniConfig config) {
        instance = config;
    }
}
