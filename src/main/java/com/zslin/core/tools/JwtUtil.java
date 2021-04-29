package com.zslin.core.tools;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    /**
     * 私钥
     */
    final static String base64EncodedSecretKey = "thisIsPrivateKey";
    /**
     * 过期时间,测试使用20分钟
     */
//    final static long TOKEN_EXP = 1000 * 60 * 20;
    final static long TOKEN_EXP = 1000 * 60 ;

    public static String getToken(String userName) {
        String token = Jwts.builder()
                .setSubject(userName)
                .claim("roles", "user")
                .setIssuedAt(new Date())
                /*过期时间*/
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXP))
                .signWith(SignatureAlgorithm.HS256, base64EncodedSecretKey)
                .compact();

        return token;
    }

    /**
     * @func token ok返回true
     * @author wangpeng
     * @date 2018/8/27 16:59
     */
    public static boolean isTokenOk(String token) {
        try {
            Jwts.parser().setSigningKey(base64EncodedSecretKey).parseClaimsJws(token).getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过authToken解析登陆信息
     * @param token
     * @return
     */
    public static String parseToken(String token) {
        try {
            String subject = Jwts.parser().setSigningKey(base64EncodedSecretKey).parseClaimsJws(token).getBody().getSubject();
            return subject;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }
}
