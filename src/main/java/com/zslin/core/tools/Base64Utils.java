package com.zslin.core.tools;

import java.net.URLDecoder;
import java.util.Base64;

/**
 * Created by zsl on 2018/7/18.
 */
public class Base64Utils {

    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            Base64.Decoder decoder = Base64.getDecoder();
            try {
                b = decoder.decode(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 解密字符串
     * 1、反转Base64
     * 2、URLDecoder
     * @param s 加密后的字符串
     * @return
     */
    public static String unPassword(String s) {
        try {
            String real = getFromBase64(s);
            real = URLDecoder.decode(real, "utf-8");
            return real;
        } catch (Exception e) {
            return "";
        }
    }
}
