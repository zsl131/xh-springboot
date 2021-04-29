package com.zslin.business.wx.tools;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客服管理工具类
 */
@Component
public class KfTools {

    @Autowired
    private WxAccessTokenTools wxAccessTokenTools;

    public String listAll() {
        String url = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token="+wxAccessTokenTools.getAccessToken();
        JSONObject jsonObj = WeixinUtil.httpRequest(url, "GET", "");
        System.out.println(jsonObj);
        return jsonObj.toJSONString();
    }

    public String sendMsg(String account, String nick, String nickname, String id, String touser, String content) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+wxAccessTokenTools.getAccessToken();
        JSONObject jsonObj = WeixinUtil.httpRequest(url, "GET", buildJson(account, nick, nickname, id, touser, content));
        System.out.println(jsonObj);
        return jsonObj.toJSONString();
    }

    private String buildJson(String account, String nick, String nickname, String id, String touser, String content) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"kf_account\":\""+account+"\",")
                .append("\"kf_nick\":\""+nick+"\",")
                .append("\"nickname\":\""+nickname+"\",")
                .append("\"touser\":\""+touser+"\",")
                .append("\"msgtype\":\"text\",")
                .append("\"kf_id\":\""+id+"\",")
                .append("\"text\": {\"content\": \"").append(content).append("\"}");
        sb.append("}");
        System.out.println(sb.toString());
        return sb.toString();
    }
}
