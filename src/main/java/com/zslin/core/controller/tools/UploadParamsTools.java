package com.zslin.core.controller.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.core.controller.dto.UploadParam;
import com.zslin.core.tools.Base64Utils;

import java.net.URLDecoder;

public class UploadParamsTools {

    /**
     * 把拓展的数据转DTO对象
     * @param extra {'w':1000, 'h':1000, 'path': '/editor'}
     * @return
     */
    public static UploadParam buildParams(String extra) {
        try {
            String params = Base64Utils.getFromBase64(extra);
            params = URLDecoder.decode(params, "utf-8");
            UploadParam obj = JSONObject.toJavaObject(JSON.parseObject(params), UploadParam.class);
            return obj;
        } catch (Exception e) {
            return new UploadParam();
        }
    }
}
