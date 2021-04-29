package com.zslin.core.controller.tools;

import com.zslin.core.controller.dto.ApiDto;
import com.zslin.core.tools.Base64Utils;
import com.zslin.core.tools.JsonParamTools;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;

@Component
public class ApiTools implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ApiDto buildApiDto(String methodType, HttpServletRequest request, String apiCode) throws NoSuchMethodException, UnsupportedEncodingException {
        String serviceName = apiCode.split("\\.")[0];
        String actionName = apiCode.split("\\.")[1];
//        Object obj = factory.getBean(serviceName);
        Object obj = getApplicationContext().getBean(serviceName);
        Method method ;
        boolean hasParams = false;
        String params = null;
        if("get".equalsIgnoreCase(methodType)) { //如果是GET请求
            params = request.getParameter("params");
        } else if("post".equalsIgnoreCase(methodType)) { //如果是POST
            params = getPostParams(request);
        } else if("app".equalsIgnoreCase(methodType)) { //如果是移动端的请求，则不用管get和post，都是一样的
            params = request.getParameter("params");
        }
        if(params==null || "".equals(params.trim())) {
            method = obj.getClass().getMethod(actionName);
        } else {
            params = Base64Utils.getFromBase64(params);
            params = URLDecoder.decode(params, "utf-8");
//                System.out.println("============="+params);

            params = JsonParamTools.rebuildParams(params, request);

            method = obj.getClass().getMethod(actionName, params.getClass());
            hasParams = true;
        }

        Class<?> userClass = ClassUtils.getUserClass(obj);
        //method代表接口中的方法，specificMethod代表实现类中的方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        String ip = request.getRemoteAddr();

        return new ApiDto(specificMethod, obj, hasParams, params, ip);
    }

    /**
     * 如果是POST请求，则获取body内容做为参数
     * @param request
     * @return
     */
    private String getPostParams(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(
                    new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            char[] bodyCharBuffer = new char[1024];
            int len = 0;
            while ((len = reader.read(bodyCharBuffer)) != -1) {
                sb.append(new String(bodyCharBuffer, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
