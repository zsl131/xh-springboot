package com.zslin.core.controller;

import com.zslin.core.annotations.NeedAuth;
import com.zslin.core.common.NormalTools;
import com.zslin.core.controller.dto.ApiDto;
import com.zslin.core.controller.tools.ApiTools;
import com.zslin.core.dto.JsonResult;
import com.zslin.core.exception.BusinessException;
import com.zslin.core.tools.AuthCheckTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping(value = "api/wx")
@Slf4j
public class ApiWeixinController {

    @Autowired
    private ApiTools apiTools;

    @Autowired
    private AuthCheckTools authCheckTools;

    @PostMapping(value = "get")
    public JsonResult post(HttpServletRequest request, HttpServletResponse response) {
        return handle(request, "post");
    }

    @GetMapping(value = "get")
    public JsonResult get(HttpServletRequest request, HttpServletResponse response) {
        return handle(request, "get");
    }

    public JsonResult handle(HttpServletRequest request, String methodType) {
        //System.out.println("----------methodType:: "+methodType);
        //String token = request.getHeader("authToken"); //身份认证token
        String openid = request.getHeader("openid"); //微信端传入的Openid
        //String nickname = Base64Utils.unPassword(request.getHeader("nickname")); //昵称
        //log.info("openid===>"+openid);
        //log.info("pwd nickname====>"+nickname);
        Long authTime = null;
        try {
            authTime = Long.parseLong(request.getHeader("authTime")); //权限时间
        } catch (Exception e) {
        }
//        logger.info("请求AuthToken：： "+token);
        String apiCode = request.getHeader("apiCode"); //接口访问编码
        if(apiCode==null || "".equals(apiCode)) {
            return JsonResult.getInstance().fail("api_code为空");
        }
        try {
            ApiDto apiDto = apiTools.buildApiDto("app", request, apiCode);
            //log.info(apiDto.toString());
            JsonResult result;

            //输出的日志，方便查看
            //log.info("移动端调用-->apiCode: {}-->params: {}", apiCode, apiDto.getParams());

            NeedAuth needAuth = apiDto.getMethod().getDeclaredAnnotation(NeedAuth.class);
            boolean hasAuth = true;
            if(needAuth!=null && needAuth.openid()) { //需要传入Openid
                hasAuth = !NormalTools.isNull(openid); //只要不空就表示有权限
            } else {
                //log.info(apiCode+"， 不需要openid验证");
            }

            if(hasAuth) {
                if(apiDto.isHasParams()) {
                    result = (JsonResult) apiDto.getMethod().invoke(apiDto.getObj(), apiDto.getParams());
                } else {
                    result = (JsonResult) apiDto.getMethod().invoke(apiDto.getObj());
                }
            } else {
//                logger.info("需要重新登陆");
                result = JsonResult.getInstance().failLogin("未检测到用户信息");
            }
            return result;
        } catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.API_ERR_FORMAT, BusinessException.Message.API_ERR_FORMAT);
        } catch (NoSuchBeanDefinitionException e) {
            return JsonResult.getInstance().fail(BusinessException.Code.NO_BEAN_DEF, BusinessException.Message.NO_BEAN_DEF);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.NO_SUCH_METHOD, BusinessException.Message.NO_SUCH_METHOD);
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.ILLEGAL_ACCESS, BusinessException.Message.ILLEGAL_ACCESS);
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
            return JsonResult.getInstance().fail(BusinessException.Code.ENCODING, BusinessException.Message.ENCODING);
        } catch (InvocationTargetException e) {
            try {
                BusinessException exc = (BusinessException) e.getTargetException();
                return JsonResult.getInstance().fail(exc.getCode(), "异常："+exc.getMsg());
            } catch (Exception ex) {
                return JsonResult.getInstance().fail("数据请求失败："+e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail("出现异常"+e.getMessage());
        }
    }
}
