package com.zslin.business.mini.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序通用工具类
 */
@Component
@Slf4j
public class MiniCommonTools {

    @Autowired
    private AccessTokenTools accessTokenTools;

    /**
     * 获取小程序码
     * @param scene 参数
     * @param auto_color
     * @param is_hyaline
     */
    public BufferedInputStream getUnlimited(String scene, boolean auto_color, boolean is_hyaline) {
        String accessToken = accessTokenTools.getAccessToken();
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken;
        Map<String, Object> map = new HashMap<>();
        map.put("scene", scene);
        map.put("auto_color", auto_color);
        map.put("is_hyaline", is_hyaline);
        BufferedInputStream bis = InternetTools.doPost(url, map);
        return bis;
    }
}
