package com.zslin.business.mini.utils;

import com.zslin.business.mini.tools.MiniConfigTools;
import com.zslin.business.wx.tools.WxAccessTokenTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by 钟述林 393156105@qq.com on 2017/1/24 23:38.
 * 与微信的数据交换
 */
@Component("miniExchangeTools")
public class MiniExchangeTools {

    @Autowired
    private WxAccessTokenTools accessTokenTools;

    @Autowired
    private MiniConfigTools miniConfigTools;

    @Autowired
    private MiniDataTools miniDataTools;

    public String saveMedia(String mediaId, String path) {
        String res = "";
        try {
            String urlStr = "https://api.weixin.qq.com/cgi-bin/media/get?access_token="+accessTokenTools.getAccessToken()+"&media_id="+mediaId;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.connect();
//            conn.getInputStream();
            Map<String, List<String>> map = conn.getHeaderFields();
            String fileNameHead = map.get("Content-disposition").get(0);
            String fileType = getFileType(fileNameHead);
            saveFile(conn.getInputStream(), path+fileType);
            res = path + fileType;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String getFileType(String str) {
        str = str.replace("\"", "");
        return str.substring(str.lastIndexOf("."));
    }

    private void saveFile(InputStream is, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            byte [] b = new byte[1024];
            int len = 0;
            while((len=is.read(b))!=-1) {
                fos.write(b, 0, len);
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
