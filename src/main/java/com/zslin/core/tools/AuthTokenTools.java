package com.zslin.core.tools;

import com.alibaba.fastjson.JSONObject;
import com.zslin.core.dto.LoginUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AuthTokenTools {

    private static Logger log = LoggerFactory.getLogger(AuthTokenTools.class);

    @Autowired
    private StringRedisTemplate template;

    //默认保留30分钟
    private static final long TIME_OUT = 60*30;

    public String buildToken(LoginUserDto user) {
        return buildToken(user, TIME_OUT);
    }

    public String buildToken(LoginUserDto user, long timeoutSeconds) {
        String subject = JSONObject.toJSONString(user);
        return buildToken(subject, timeoutSeconds);
    }

    public String buildToken(String subject) {
        return buildToken(subject, TIME_OUT);
    }

    public String buildToken(String subject, long timeoutSeconds) {
        String token = JwtUtil.getToken(subject);
        log.debug("加密之后：："+SecurityUtil.encode(subject));
        template.opsForValue().set(token, SecurityUtil.encode(subject), timeoutSeconds, TimeUnit.SECONDS);
        return token;
    }

    /**
     * 通过传入的token刷新缓存数据
     * @param token
     * @return 返回true表示未过期并刷新成功，返回false则刷新失败，需要重新登陆
     */
    public boolean refreshToken(String token, Long timeOutSeconds) {
        timeOutSeconds = timeOutSeconds == null?TIME_OUT:timeOutSeconds; //如果是空则使用默认
        String subject = template.opsForValue().get(token);
        if(subject!=null) {
            template.opsForValue().set(token, subject, timeOutSeconds, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    public boolean refreshToken(String token) {
        return refreshToken(token, TIME_OUT);
    }

    public String getAuthSubject(String token) {
        try {
            String subject = template.opsForValue().get(token);
            return SecurityUtil.decode(subject);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isAuthOk(String token) {
        String subject = getAuthSubject(token);
        if(subject==null) {return false;}
        return true;
    }

    /**
     * 解决成LoginUserDto对象
     * @param token
     * @return
     */
    public LoginUserDto parseToken(String token) {
        String subject = getAuthSubject(token);
        if(subject==null) {return null;}
        try {
            JSONObject jsonObj = JSONObject.parseObject(subject);
            refreshToken(token); //每一次请求都需要刷新一次token缓存
            return JSONObject.toJavaObject(jsonObj, LoginUserDto.class);
        } catch (Exception e) {
            return null;
        }
    }
}
