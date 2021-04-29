package com.zslin.core.controller;

import com.zslin.core.annotations.NeedAuth;
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
@RequestMapping(value = "api")
@Slf4j
public class ApiController {

    @Autowired
    private AuthCheckTools authCheckTools;

    @Autowired
    private ApiTools apiTools;

    @PostMapping(value = "get")
    public JsonResult post(HttpServletRequest request, HttpServletResponse response) {
        return handle(request, "post");
    }

    @GetMapping(value = "get")
    public JsonResult get(HttpServletRequest request, HttpServletResponse response) {
        return handle(request, "get");
    }

    public JsonResult handle(HttpServletRequest request, String methodType) {
        String token = request.getHeader("authToken"); //身份认证token
        Long authTime = null;
        try {
            authTime = Long.parseLong(request.getHeader("authTime")); //权限时间，单位秒
        } catch (Exception e) {
        }

        String apiCode = request.getHeader("apiCode"); //接口访问编码

        if(apiCode==null || "".equals(apiCode)) {
            return JsonResult.getInstance().fail("apiCode不能为空");
        }

        try {

            ApiDto apiDto = apiTools.buildApiDto(methodType, request, apiCode);

            //输出的日志，方便查看
            //log.info("接口调用，apiCode: {}, params: {}", apiCode, apiDto.getParams());

            JsonResult result;

            NeedAuth needAuth = apiDto.getMethod().getDeclaredAnnotation(NeedAuth.class);
            //System.out.println("---------->needAuth:::"+needAuth);
            boolean hasAuth = true;
            if(needAuth==null || needAuth.need()) { //需要权限验证
                //log.info(apiCode+"，需要权限验证");
                hasAuth = authCheckTools.hasAuth(token, authTime);
            } else {
                //log.info(apiCode+"， 不需要权限验证");
            }

            if(hasAuth) {
                if(apiDto.isHasParams()) {
                    result = (JsonResult) apiDto.getMethod().invoke(apiDto.getObj(), apiDto.getParams());
                } else {
                    result = (JsonResult) apiDto.getMethod().invoke(apiDto.getObj());
                }
            } else {
//                logger.info("需要重新登陆");
                //System.out.println("-------->apiCode:" + apiCode);
                result = JsonResult.getInstance().failLogin("无权访问，请先登陆");
            }
            return result;
            //IllegalAccessException, IllegalArgumentException,InvocationTargetException
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
                String msg = e.getTargetException().getMessage();
                e.getTargetException().printStackTrace();
                return JsonResult.getInstance().fail("数据请求失败："+msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.getInstance().fail("出现异常"+e.getMessage());
        }
    }
}
