package com.zslin.core.tools;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;

@Configuration
@Component
@Data
public class ConfigTools {

    @Value("${config.filePath}")
    private String filePath;

    @Value("${spring.application.name}")
    private String appName;

    public String getFilePath() {
        return getFilePath("");
    }

    public String getFilePath(String basePath) {
        File f = new File(filePath+basePath);
        if(!f.exists()) {f.mkdirs();}
        return f.getAbsolutePath()+File.separator;
    }
}
