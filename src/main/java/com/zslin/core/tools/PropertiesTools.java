package com.zslin.core.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件管理工具
 */
@Component
public class PropertiesTools {

    @Autowired
    private ConfigTools configTools;

    public Properties getProperties(String fileName, boolean isClasspath) {
        try {
            if(isClasspath) {
                fileName = "classpath:/"+fileName;
            } else {
                fileName = "file:"+configTools.getFilePath() + fileName;
            }
            return PropertiesLoaderUtils.loadProperties(new PathMatchingResourcePatternResolver().getResource(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
