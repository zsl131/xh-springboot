package com.zslin.business.mini.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zslin.business.mini.dto.NewCustomDto;
import com.zslin.business.model.Orders;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;

/**
 * 小程序常用工具类
 */
public class MiniUtils {

    /**
     * 生成代理信息
     * @param orders
     * @return
     */
    public static String buildAgent(Orders orders) {
        try {
            StringBuffer res = new StringBuffer();
            res.append(orders.getAgentName()==null?"无代理":orders.getAgentName())
                    .append("推荐").append(orders.getNickname());
            return res.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 小程序解密用户数据
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    public static NewCustomDto decryptionUserInfo(String encryptedData, String sessionKey, String iv) {
        String str = decryption(encryptedData, sessionKey, iv);
        if(str!=null) {
            return JSONObject.toJavaObject(JSON.parseObject(str), NewCustomDto.class);
        }
        return null;
    }

    public static String getPhone(String encryptedData, String sessionKey, String iv) {
        return decryption(encryptedData, sessionKey, iv);
    }

    private static String decryption(String encryptedData, String sessionKey, String iv) {
        encryptedData = encryptedData.replaceAll(" ", "+"); //传入之后+号全部自动变成了空格
        iv = iv.replaceAll(" ", "+");
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);

        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
//                String result = new String(resultByte, "UTF-8");
//                NewCustomDto dto = JSONObject.toJavaObject(JSON.parseObject(result), NewCustomDto.class);
                return new String(resultByte, "UTF-8");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
