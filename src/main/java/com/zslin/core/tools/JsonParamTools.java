package com.zslin.core.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by zsl on 2018/8/10.
 */
public class JsonParamTools {

    public static final String HEADER_PARAM_NAME = "headerParams";

    public static String rebuildParams(String params, HttpServletRequest request)  {

        List<String> ignoreNames = new ArrayList<>();

        ignoreNames.add("accept-language");
        ignoreNames.add("accept-encoding");
        ignoreNames.add("referer");
        ignoreNames.add("accept");
        ignoreNames.add("authToken"); //这两个不用传给实际服务接口
        ignoreNames.add("apiCode");
        ignoreNames.add("apicode");
        ignoreNames.add("user-agent");
        ignoreNames.add("connection");
        ignoreNames.add("host");
        ignoreNames.add("pragma");
        ignoreNames.add("content-type");
        ignoreNames.add("cache-control");
        ignoreNames.add("sec-fetch-mode");
        ignoreNames.add("sec-fetch-site");
        ignoreNames.add("same-origin");
        ignoreNames.add("cookie");
        String ip = request.getRemoteAddr(); //IP地址

        Enumeration<String> names = request.getHeaderNames();
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("ip", ip);
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            if(!ignoreNames.contains(name)) {
                if("nickname".equalsIgnoreCase(name) || "username".equalsIgnoreCase(name)) { //如果是nickname则需要解密
                    headerMap.put(name, Base64Utils.unPassword(request.getHeader(name)));
                } else {
                    headerMap.put(name, request.getHeader(name));
                }
            }
        }

        JSONObject jsonObj = JSON.parseObject(params);
        if(!headerMap.isEmpty()) {
            jsonObj.put(HEADER_PARAM_NAME, headerMap);
        }
        String result = jsonObj.toJSONString();
        return result;
    }

    public static String getHeaderParams(String params) {
        return JsonTools.getJsonParam(params, HEADER_PARAM_NAME);
    }

    public static Map<String, String> getHeaderMap(String params) {
        Map<String, String> result = new HashMap<>();
        try {
            JSONObject jsonObj = JSON.parseObject(getHeaderParams(params));
            Iterator<String> keys = jsonObj.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                result.put(key, jsonObj.getString(key));
            }
        } catch (Exception e) {
        }
        return result;
    }
}
