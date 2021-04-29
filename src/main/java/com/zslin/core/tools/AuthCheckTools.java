package com.zslin.core.tools;

import com.zslin.core.dto.LoginUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 检查是否有权限访问Api接口
 */
@Component
public class AuthCheckTools {

//    Logger log = LoggerFactory.getLogger(AuthCheckTools.class);

    @Autowired
    private AuthTokenTools authTokenTools;

    public boolean hasAuth(String token) {
        return hasAuth(token, null); //TODO 现在默认都有权限访问
    }

    public boolean hasAuth(String token, Long refreshTime) {
        LoginUserDto user = authTokenTools.parseToken(token);
        if(user==null) {return false;}
        if(refreshTime==null || refreshTime<=0) {
            authTokenTools.refreshToken(token);
        } else {
            authTokenTools.refreshToken(token, refreshTime); //每次请求之后都需要刷新权限，这样才能保证权限不过期
        }
        return true; //TODO 现在默认都有权限访问
    }
}
