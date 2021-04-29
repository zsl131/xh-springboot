package com.zslin.business.mini.tools;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 二维码工具类
 */
@Component
public class QrTools {

    @Autowired
    private AccessTokenTools  accessTokenTools;

    public BufferedImage getQrB(String page, String scene) {
        String accessToken = accessTokenTools.getAccessToken();
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken;

        try {
            URL uri = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setRequestProperty("Content-type", "application/json");
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            paramJson.put("scene", scene);
            paramJson.put("page", page);
            paramJson.put("width", 190);
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();

            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());

            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            //buff用于存放循环读取的临时数据
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = bis.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }


            ByteArrayInputStream inputStream= new ByteArrayInputStream(swapStream.toByteArray());
            BufferedImage image = ImageIO.read(inputStream);
            return image;
            /**裁剪原图  目前访问微信 微信返回的是 470*535 像素 170620*/
            /*BufferedImage subImage = image.getSubimage(0, 0, image.getWidth(), image.getHeight());


            BufferedImage inputbig = new BufferedImage(190, 190, BufferedImage.TYPE_INT_BGR);
            Graphics2D g = (Graphics2D) inputbig.getGraphics();
            g.drawImage(subImage, 0, 0,190,190,null); //画图
            g.dispose();
            inputbig.flush();
            ImageIO.write(inputbig, "jpg", new File("D:/temp/123.jpg"));*/
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
